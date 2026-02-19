"use client"

import { useState, useEffect, useMemo, useDeferredValue } from "react"
import { OpeningGrid } from "@/components/opening-grid"
import { OpeningSummary } from "@/components/opening-summary"
import { OpeningFilter } from "@/components/opening-filter" // Import the new filter component
import { Skeleton } from "@/components/ui/skeleton"
import { provideAllOpeningResult, provideSummary } from "@/lib/provide/provideFacade"
import { calculateRatesFromCounts } from "@/lib/stats"
import type { OpeningStatView, ColorFilter, DisplaySummary, Stat, WinRate, SortBy } from "@/lib/types"

function calculateDisplaySummary(
  winRates: WinRate[],
  bestWinRateOpenings: Stat[],
  mostPlayedOpenings: Stat[],
  filter: ColorFilter
): DisplaySummary {
  const filteredWinRates = filter === "all" ? winRates : winRates.filter((rate) => rate.color === filter);
  const totalWins = filteredWinRates.reduce((sum, rate) => sum + rate.wins, 0);
  const totalDraws = filteredWinRates.reduce((sum, rate) => sum + rate.draws, 0);
  const totalLosses = filteredWinRates.reduce((sum, rate) => sum + rate.losses, 0);
  const rates = calculateRatesFromCounts(totalWins, totalDraws, totalLosses)

  const filteredBestWinRateOpenings = filter === "all" ? bestWinRateOpenings : bestWinRateOpenings.filter((op) => op.color === filter);
  const filteredMostPlayedOpenings = filter === "all" ? mostPlayedOpenings : mostPlayedOpenings.filter((op) => op.color === filter);

  return {
    totalWins,
    totalDraws,
    totalLosses,
    totalGames: rates.totalGames,
    winRate: rates.winRate,
    drawRate: rates.drawRate,
    lossRate: rates.lossRate,
    bestWinRateOpenings: filteredBestWinRateOpenings,
    mostPlayedOpenings: filteredMostPlayedOpenings,
  };
}

export default function HomePage() {
  const [allOpenings, setAllOpenings] = useState<OpeningStatView[]>([]) // Store all fetched openings
  const [summaries, setSummaries] = useState<Record<ColorFilter, DisplaySummary> | null>(null)
  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames") // Moved from OpeningGrid
  const [search, setSearch] = useState("") // Moved from OpeningGrid
  const deferredSearch = useDeferredValue(search)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    async function load() {
      setLoading(true)
      setError(null)

      try {
        const [openingsData, summaryData] = await Promise.all([
          provideAllOpeningResult(),
          provideSummary(),
        ])
        if (!cancelled) {
          setAllOpenings(openingsData) // Set all fetched openings
          const { winRates, bestWinRateOpenings, mostPlayedOpenings } = summaryData;
          setSummaries({
            all: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "all"),
            white: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "white"),
            black: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "black"),
          });
        }
      } catch (err) {
        console.error("Failed to load data", err)
        if (!cancelled) {
          setError("Failed to load opening stats. Please try again.")
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    load()

    return () => {
      cancelled = true
    }
  }, [])

  // Filtering and sorting logic moved to HomePage
  const filteredAndSortedOpenings = useMemo(() => {
    let list = [...allOpenings]

    // 색상 필터
    if (colorFilter !== "all") {
      list = list.filter((s) => s.color === colorFilter)
    }

    // 검색 필터
    if (deferredSearch.trim()) {
      const q = deferredSearch.toLowerCase()
      list = list.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.eco.toLowerCase().includes(q)
      )
    }

    // 정렬
    list.sort((a, b) => {
      switch (sortBy) {
        case "winRate":
          return b.winRate - a.winRate
        case "totalGames":
          return b.totalGames - a.totalGames
        case "name":
          return a.name.localeCompare(b.name)
        default:
          return 0
      }
    })

    return list
  }, [allOpenings, colorFilter, deferredSearch, sortBy])

  const currentSummary = summaries ? summaries[colorFilter] : null;

  return (
    <div className="min-h-screen overflow-x-hidden bg-background">
      <main className="mx-auto max-w-screen-2xl px-4 py-6 lg:px-6">
        {loading ? (
          <LoadingSkeleton />
        ) : error ? (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
            {error}
          </div>
        ) : (
          <div className="flex flex-col gap-6">
            {/* Filter and Search UI at the top */}
            <OpeningFilter
              colorFilter={colorFilter}
              onColorFilterChange={setColorFilter}
              sortBy={sortBy}
              onSortByChange={setSortBy}
              search={search}
              onSearchChange={setSearch}
            />

            {/* Summary Section */}
            {currentSummary && <OpeningSummary summary={currentSummary} colorFilter={colorFilter} />}
            
            {/* Opening Grid */}
            <OpeningGrid stats={filteredAndSortedOpenings} />
          </div>
        )}
      </main>
    </div>
  )
}



function LoadingSkeleton() {
  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-4">
        <Skeleton className="h-5 w-32" />
        <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-20 rounded-lg" />
          ))}
        </div>
        <Skeleton className="h-10 rounded-lg" />
      </div>
      <div className="flex flex-col gap-2 sm:flex-row sm:justify-between">
        <Skeleton className="h-10 w-64" />
        <div className="flex gap-2">
          <Skeleton className="h-10 w-56" />
          <Skeleton className="h-10 w-40" />
        </div>
      </div>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {Array.from({ length: 9 }).map((_, i) => (
          <Skeleton key={i} className="h-32 rounded-lg" />
        ))}
      </div>
    </div>
  )
}
