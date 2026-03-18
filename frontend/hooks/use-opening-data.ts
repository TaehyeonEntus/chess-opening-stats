"use client"

import { useState, useEffect, useMemo, useDeferredValue, useCallback, useRef } from "react"
import { fetchDashboard } from "@/lib/api/api"
import { adaptColorOpeningStat } from "@/lib/adapters"
import { parseOpeningCsv, type OpeningDictionary } from "@/lib/openings/csv-parser"
import { calculateRatesFromCounts } from "@/lib/stats"
import type { OpeningStatView, ColorFilter, DisplaySummary, WinRate, SortBy } from "@/lib/types"

function calculateDisplaySummary(
  winRates: WinRate[],
  allOpenings: OpeningStatView[],
  filter: ColorFilter,
  providedMostPlayed?: OpeningStatView[],
  providedBestWinRate?: OpeningStatView[]
): DisplaySummary {
  const filteredWinRates = filter === "all" ? winRates : winRates.filter((r) => r.color === filter)
  const totalWins = filteredWinRates.reduce((sum, r) => sum + r.wins, 0)
  const totalDraws = filteredWinRates.reduce((sum, r) => sum + r.draws, 0)
  const totalLosses = filteredWinRates.reduce((sum, r) => sum + r.losses, 0)
  const rates = calculateRatesFromCounts(totalWins, totalDraws, totalLosses)

  const openingsForFilter = filter === "all" ? allOpenings : allOpenings.filter((op) => op.color === filter)
  
  const mostPlayedOpenings = providedMostPlayed || [...openingsForFilter]
    .sort((a, b) => b.totalGames - a.totalGames)
    .slice(0, 5)

  const bestWinRateOpenings = providedBestWinRate || [...openingsForFilter]
    .filter(op => op.totalGames >= 10) // 최소 10게임 이상인 것 중 승률 높은 순
    .sort((a, b) => b.winRate - a.winRate)
    .slice(0, 5)

  return {
    totalWins,
    totalDraws,
    totalLosses,
    totalGames: rates.totalGames,
    winRate: rates.winRate,
    drawRate: rates.drawRate,
    lossRate: rates.lossRate,
    bestWinRateOpenings,
    mostPlayedOpenings,
  }
}

export function useOpeningData() {
  const [allOpenings, setAllOpenings] = useState<OpeningStatView[]>([])
  const [openingDictionary, setOpeningDictionary] = useState<OpeningDictionary | null>(null)
  const [summaries, setSummaries] = useState<Record<ColorFilter, DisplaySummary> | null>(null)
  const [loading, setLoading] = useState(false)
  const [isPolling, setIsPolling] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames")
  const [minGames, setMinGames] = useState("")
  const [maxGames, setMaxGames] = useState("")
  const [search, setSearch] = useState("")
  const deferredSearch = useDeferredValue(search)

  const pollingTimerRef = useRef<NodeJS.Timeout | null>(null)
  const pollCountRef = useRef(0)

  const stopPolling = useCallback(() => {
    if (pollingTimerRef.current) {
      clearTimeout(pollingTimerRef.current)
      pollingTimerRef.current = null
    }
    setIsPolling(false)
    pollCountRef.current = 0
  }, [])

  const loadData = useCallback(async (platform: string, username: string) => {
    setLoading(true)
    setError(null)
    stopPolling()

    try {
      const csvResponse = await fetch('/data/openings/OPENING.csv')
        .then(res => {
          if (!res.ok) throw new Error("CSV file not found")
          return res.text()
        })
      const dictionary = parseOpeningCsv(csvResponse)
      setOpeningDictionary(dictionary)

      const poll = async () => {
        try {
          const dashboard = await fetchDashboard(platform, username)
          
          if (!dashboard || (!dashboard.white && !dashboard.black)) {
            pollCountRef.current += 1
            if (pollCountRef.current < 30) {
              setIsPolling(true)
              pollingTimerRef.current = setTimeout(poll, 1500)
            } else {
              setIsPolling(false)
              setLoading(false)
              setError("Data not ready yet.")
            }
            return
          }

          setLoading(false)
          setIsPolling(false)
          stopPolling()

          const whiteOpenings = adaptColorOpeningStat(dashboard.white?.openings || [], "white", dictionary)
          const blackOpenings = adaptColorOpeningStat(dashboard.black?.openings || [], "black", dictionary)
          
          const whiteMostPlayed = adaptColorOpeningStat(dashboard.white?.mostPlayedOpenings || [], "white", dictionary)
          const whiteBestWinRate = adaptColorOpeningStat(dashboard.white?.highestWinRateOpenings || [], "white", dictionary)
          
          const blackMostPlayed = adaptColorOpeningStat(dashboard.black?.mostPlayedOpenings || [], "black", dictionary)
          const blackBestWinRate = adaptColorOpeningStat(dashboard.black?.highestWinRateOpenings || [], "black", dictionary)

          const combined = [...whiteOpenings, ...blackOpenings]
          setAllOpenings(combined)
          
          // Combine top openings from both sides for the 'all' summary
          const combinedMostPlayed = [...whiteMostPlayed, ...blackMostPlayed]
            .sort((a, b) => b.totalGames - a.totalGames)
            .slice(0, 5)
            
          const combinedBestWinRate = [...whiteBestWinRate, ...blackBestWinRate]
            .filter(op => op.totalGames >= 10)
            .sort((a, b) => b.winRate - a.winRate)
            .slice(0, 5)

          const winRates: WinRate[] = [
            { 
              color: "white", 
              wins: dashboard.white?.stat?.win || 0, 
              draws: dashboard.white?.stat?.draw || 0, 
              losses: dashboard.white?.stat?.lose || 0 
            },
            { 
              color: "black", 
              wins: dashboard.black?.stat?.win || 0, 
              draws: dashboard.black?.stat?.draw || 0, 
              losses: dashboard.black?.stat?.lose || 0 
            },
          ]
          
          setSummaries({
            all: calculateDisplaySummary(winRates, combined, "all", combinedMostPlayed, combinedBestWinRate),
            white: calculateDisplaySummary(winRates, whiteOpenings, "white", whiteMostPlayed, whiteBestWinRate),
            black: calculateDisplaySummary(winRates, blackOpenings, "black", blackMostPlayed, blackBestWinRate),
          })
        } catch (err) {
          console.error("Poll failed", err)
          setIsPolling(false)
          setLoading(false)
        }
      }

      await poll()
    } catch (err) {
       setError(err instanceof Error ? err.message : "An error occurred")
       setLoading(false)
    }
  }, [stopPolling])

  useEffect(() => {
    return () => stopPolling()
  }, [stopPolling])

  const epdMap = useMemo(() => {
    if (!openingDictionary) return new Map<string, { id: number; eco: string; name: string; epd: string }>()
    const map = new Map<string, { id: number; eco: string; name: string; epd: string }>()
    Object.entries(openingDictionary).forEach(([idStr, data]) => {
      const id = Number(idStr)
      if (data.epd) {
        map.set(data.epd, { ...data, id })
      }
    })
    return map
  }, [openingDictionary])

  const filteredAndSortedOpenings = useMemo(() => {
    let list = [...allOpenings]
    if (colorFilter !== "all") list = list.filter((s) => s.color === colorFilter)
    if (deferredSearch.trim()) {
      const q = deferredSearch.toLowerCase()
      list = list.filter((s) => s.name.toLowerCase().includes(q) || s.eco.toLowerCase().includes(q))
    }
    const parsedMin = Number(minGames)
    const parsedMax = Number(maxGames)
    if (minGames.trim() !== "" && !Number.isNaN(parsedMin)) list = list.filter((s) => s.totalGames >= parsedMin)
    if (maxGames.trim() !== "" && !Number.isNaN(parsedMax)) list = list.filter((s) => s.totalGames <= parsedMax)

    list.sort((a, b) => {
      switch (sortBy) {
        case "winRate": return b.winRate - a.winRate
        case "totalGames": return b.totalGames - a.totalGames
        case "name": return a.name.localeCompare(b.name)
        default: return 0
      }
    })
    return list
  }, [allOpenings, colorFilter, deferredSearch, minGames, maxGames, sortBy])

  return {
    allOpenings,
    openingDictionary,
    epdMap,
    filteredAndSortedOpenings,
    summaries,
    currentSummary: summaries ? summaries[colorFilter] : null,
    loading,
    isPolling,
    error,
    loadData,
    clearData: () => {
      setAllOpenings([])
      setSummaries(null)
      setError(null)
      setLoading(false)
      stopPolling()
    },
    colorFilter,
    setColorFilter,
    sortBy,
    setSortBy,
    minGames,
    setMinGames,
    maxGames,
    setMaxGames,
    search,
    setSearch,
  }
}
