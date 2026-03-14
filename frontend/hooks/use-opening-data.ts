"use client"

import { useState, useEffect, useMemo, useDeferredValue, useCallback, useRef } from "react"
import { fetchDashboard, type DashboardResponse } from "@/lib/api/api"
import { adaptColorOpeningStat, adaptStat } from "@/lib/provide/internel/adapter"
import { parseOpeningCsv, type OpeningDictionary } from "@/lib/openings/csv-parser"
import { calculateRatesFromCounts } from "@/lib/stats"
import { toast } from "sonner"
import type { OpeningStatView, ColorFilter, DisplaySummary, WinRate, Stat, SortBy } from "@/lib/types"

function calculateDisplaySummary(
  winRates: WinRate[],
  bestWinRateOpenings: Stat[],
  mostPlayedOpenings: Stat[],
  filter: ColorFilter
): DisplaySummary {
  const filteredWinRates = filter === "all" ? winRates : winRates.filter((r) => r.color === filter)
  const totalWins = filteredWinRates.reduce((sum, r) => sum + r.wins, 0)
  const totalDraws = filteredWinRates.reduce((sum, r) => sum + r.draws, 0)
  const totalLosses = filteredWinRates.reduce((sum, r) => sum + r.losses, 0)
  const rates = calculateRatesFromCounts(totalWins, totalDraws, totalLosses)

  return {
    totalWins,
    totalDraws,
    totalLosses,
    totalGames: rates.totalGames,
    winRate: rates.winRate,
    drawRate: rates.drawRate,
    lossRate: rates.lossRate,
    bestWinRateOpenings: filter === "all" ? bestWinRateOpenings : bestWinRateOpenings.filter((op) => op.color === filter),
    mostPlayedOpenings: filter === "all" ? mostPlayedOpenings : mostPlayedOpenings.filter((op) => op.color === filter),
  }
}

export function useOpeningData() {
  const [allOpenings, setAllOpenings] = useState<OpeningStatView[]>([])
  const [openingDictionary, setOpeningDictionary] = useState<OpeningDictionary | null>(null)
  const [summaries, setSummaries] = useState<Record<ColorFilter, DisplaySummary> | null>(null)
  const [loading, setLoading] = useState(false)
  const [isPolling, setIsPolling] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Filter state
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
    pollCountRef.current = 0
    setIsPolling(false)
  }, [])

  const loadData = useCallback(async (platform: string, username: string) => {
    setLoading(true)
    setError(null)
    stopPolling()

    try {
      // 1. Fetch CSV Dictionary first
      const csvResponse = await fetch('/data/openings/OPENING.csv')
        .then(res => {
          if (!res.ok) throw new Error("CSV file not found")
          return res.text()
        })
        .catch(e => {
          console.error("CSV fetch failed", e)
          return ""
        })
      const dictionary = parseOpeningCsv(csvResponse)
      setOpeningDictionary(dictionary)

      // 2. Start polling for dashboard
      const poll = async () => {
        try {
          const dashboard = await fetchDashboard(platform, username)
          
          // Data is not ready if response is null, empty string, or missing both white and black data
          const isNotReady = !dashboard || 
                           (typeof dashboard === 'object' && Object.keys(dashboard).length === 0) ||
                           (dashboard.white === undefined && dashboard.black === undefined);

          if (isNotReady) {
            // Data not ready yet, continue polling
            setIsPolling(true)
            pollingTimerRef.current = setTimeout(poll, 1000) // Poll every 1 second
            return
          }

          // Data received!
          setLoading(false)
          
          // Initial data is loaded, so we can hide the "Background Syncing" message.
          // Further polls will be silent.
          if (pollCountRef.current === 0) {
            setIsPolling(false)
          }

          const whiteStats = dashboard.white?.openings || []
          const blackStats = dashboard.black?.openings || []
          
          const whiteOpenings = adaptColorOpeningStat(whiteStats, "white", dictionary)
          const blackOpenings = adaptColorOpeningStat(blackStats, "black", dictionary)
          setAllOpenings([...whiteOpenings, ...blackOpenings])
          
          // Calculate win rates
          const winRates: WinRate[] = [
            { 
              color: "white", 
              wins: dashboard.white?.record?.win || 0, 
              draws: dashboard.white?.record?.draw || 0, 
              losses: dashboard.white?.record?.lose || 0 
            },
            { 
              color: "black", 
              wins: dashboard.black?.record?.win || 0, 
              draws: dashboard.black?.record?.draw || 0, 
              losses: dashboard.black?.record?.lose || 0 
            },
          ]
          
          // Calculate best win rate
          const bestWinRateOpenings: Stat[] = [
            ...adaptStat(dashboard.white?.highestWinRateOpenings || [], "white", dictionary),
            ...adaptStat(dashboard.black?.highestWinRateOpenings || [], "black", dictionary)
          ]
          
          // Calculate most played
          const mostPlayedOpenings: Stat[] = [
            ...adaptStat(dashboard.white?.mostPlayedOpenings || [], "white", dictionary),
            ...adaptStat(dashboard.black?.mostPlayedOpenings || [], "black", dictionary)
          ]

          setSummaries({
            all: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "all"),
            white: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "white"),
            black: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "black"),
          })

          // Continue polling in background at a slower rate (e.g. 10 seconds)
          // Stop after 10 polls (about 1.5 minutes of background syncing)
          pollCountRef.current += 1
          if (pollCountRef.current < 10) {
            pollingTimerRef.current = setTimeout(poll, 10000)
          } else {
            setIsPolling(false)
          }
        } catch (err) {
          console.error("Dashboard poll failed", err)
          setIsPolling(false)
          setLoading(false)
        }
      }

      await poll()

    } catch (err) {
       console.error("Failed to load data", err)
       setError(err instanceof Error ? err.message : "An error occurred")
       setLoading(false)
    }
  }, [stopPolling])

  useEffect(() => {
    return () => stopPolling()
  }, [stopPolling])

  const filteredAndSortedOpenings = useMemo(() => {
    let list = [...allOpenings]

    if (colorFilter !== "all") {
      list = list.filter((s) => s.color === colorFilter)
    }

    if (deferredSearch.trim()) {
      const q = deferredSearch.toLowerCase()
      list = list.filter((s) => s.name.toLowerCase().includes(q) || s.eco.toLowerCase().includes(q))
    }

    const parsedMin = Number(minGames)
    const parsedMax = Number(maxGames)
    if (minGames.trim() !== "" && !Number.isNaN(parsedMin)) {
      list = list.filter((s) => s.totalGames >= parsedMin)
    }
    if (maxGames.trim() !== "" && !Number.isNaN(parsedMax)) {
      list = list.filter((s) => s.totalGames <= parsedMax)
    }

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

  const currentSummary = summaries ? summaries[colorFilter] : null

  const epdMap = useMemo(() => {
    if (!openingDictionary) return new Map<string, { id: number; eco: string; name: string; epd: string }>()
    const map = new Map<string, { id: number; eco: string; name: string; epd: string }>()
    Object.entries(openingDictionary).forEach(([idStr, meta]) => {
      const id = parseInt(idStr)
      map.set(meta.epd, { id, ...meta })
    })
    return map
  }, [openingDictionary])

  const clearData = useCallback(() => {
    setAllOpenings([])
    setSummaries(null)
    setError(null)
    setLoading(false)
    stopPolling()
    pollCountRef.current = 0
  }, [stopPolling])

  return {
    allOpenings,
    openingDictionary,
    epdMap,
    filteredAndSortedOpenings,
    summaries,
    currentSummary,
    loading,
    isPolling,
    error,
    loadData,
    clearData,
    // filter state
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
