"use client"

import { useState, useEffect, useMemo, useDeferredValue, useCallback } from "react"
import { provideHomeView } from "@/lib/provide/provideFacade"
import { adaptColorOpeningStat, adaptStat } from "@/lib/provide/internel/adapter"
import { parseOpeningCsv } from "@/lib/openings/csv-parser"
import { calculateRatesFromCounts } from "@/lib/stats"
import type { OpeningStatView, ColorFilter, DisplaySummary, WinRate, Stat, SortBy, HomeView } from "@/lib/types"

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

export interface FilterState {
  colorFilter: ColorFilter
  sortBy: SortBy
  minGames: string
  maxGames: string
  search: string
}

export function useOpeningData() {
  const [allOpenings, setAllOpenings] = useState<OpeningStatView[]>([])
  const [summaries, setSummaries] = useState<Record<ColorFilter, DisplaySummary> | null>(null)
  const [nickname, setNickname] = useState<string>("")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Filter state
  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames")
  const [minGames, setMinGames] = useState("")
  const [maxGames, setMaxGames] = useState("")
  const [search, setSearch] = useState("")
  const deferredSearch = useDeferredValue(search)

  const loadData = useCallback(async (showLoading = true) => {
    if (showLoading) setLoading(true)
    setError(null)

    try {
      const [homeView, csvResponse] = await Promise.all([
        provideHomeView(),
        fetch('/data/openings/OPENING.csv').then(res => res.text())
      ])
      
      const dictionary = parseOpeningCsv(csvResponse)
      
      const whiteOpenings = adaptColorOpeningStat(homeView.white.openings, "white", dictionary)
      const blackOpenings = adaptColorOpeningStat(homeView.black.openings, "black", dictionary)
      setAllOpenings([...whiteOpenings, ...blackOpenings])
      
      setNickname(homeView.account.nickname)
      
      // Calculate win rates
      const winRates: WinRate[] = [
        { color: "white", wins: homeView.white.record.win, draws: homeView.white.record.draw, losses: homeView.white.record.lose },
        { color: "black", wins: homeView.black.record.win, draws: homeView.black.record.draw, losses: homeView.black.record.lose },
      ]
      
      // Calculate best win rate
      const bestWinRateOpenings: Stat[] = [
        ...adaptStat(homeView.white.highestWinRateOpenings, "white", dictionary),
        ...adaptStat(homeView.black.highestWinRateOpenings, "black", dictionary)
      ]
      
      // Calculate most played
      const mostPlayedOpenings: Stat[] = [
        ...adaptStat(homeView.white.mostPlayedOpenings, "white", dictionary),
        ...adaptStat(homeView.black.mostPlayedOpenings, "black", dictionary)
      ]

      setSummaries({
        all: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "all"),
        white: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "white"),
        black: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "black"),
      })

    } catch (err) {
       console.error("Failed to load data", err)
       setError("Failed to load opening stats. Please try again.")
    } finally {
       if (showLoading) setLoading(false)
    }
  }, [])

  useEffect(() => {
    let cancelled = false
    async function load() {
      if (!cancelled) {
        await loadData(true)
      }
    }
    load()
    return () => { cancelled = true }
  }, [loadData])

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

  return {
    allOpenings,
    filteredAndSortedOpenings,
    summaries,
    currentSummary,
    nickname,
    loading,
    error,
    loadData,
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
