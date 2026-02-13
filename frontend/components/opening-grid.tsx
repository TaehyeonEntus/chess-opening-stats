"use client"

import { useState, useMemo } from "react"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { OpeningCard } from "@/components/opening-card"
import { StatsSummary } from "@/components/stats-summary"
import type { Opening, OpeningResult, ColorFilter, SortBy } from "@/lib/types"
import { mergeOpeningData, calcSummary } from "@/lib/calc"
import { Search, ArrowDownWideNarrow } from "lucide-react"

interface OpeningGridProps {
  openings: Opening[]
  results: OpeningResult[]
  isLoggedIn: boolean
}

export function OpeningGrid({ openings, results, isLoggedIn }: OpeningGridProps) {
  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames")
  const [search, setSearch] = useState("")

  const stats = useMemo(
    () => mergeOpeningData(openings, results, colorFilter),
    [openings, results, colorFilter]
  )

  const summary = useMemo(() => calcSummary(stats), [stats])

  const filtered = useMemo(() => {
    let list = stats

    if (search.trim()) {
      const q = search.toLowerCase()
      list = list.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.eco.toLowerCase().includes(q)
      )
    }

    return list.sort((a, b) => {
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
  }, [stats, search, sortBy])

  return (
    <div className="flex flex-col gap-6">
      <StatsSummary summary={summary} isLoggedIn={isLoggedIn} />

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <Tabs
          value={colorFilter}
          onValueChange={(v) => setColorFilter(v as ColorFilter)}
        >
          <TabsList>
            <TabsTrigger value="all">All</TabsTrigger>
            <TabsTrigger value="white" className="gap-1.5">
              <span className="inline-flex h-3 w-3 items-center justify-center rounded-full border border-foreground/20 bg-background" />
              White
            </TabsTrigger>
            <TabsTrigger value="black" className="gap-1.5">
              <span className="inline-flex h-3 w-3 items-center justify-center rounded-full border border-foreground/20 bg-foreground" />
              Black
            </TabsTrigger>
          </TabsList>
        </Tabs>

        <div className="flex items-center gap-2">
          <div className="relative flex-1 sm:w-56 sm:flex-none">
            <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Search opening or ECO..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="pl-9"
            />
          </div>
          <Select value={sortBy} onValueChange={(v) => setSortBy(v as SortBy)}>
            <SelectTrigger className="w-40 gap-1.5">
              <ArrowDownWideNarrow className="h-4 w-4 text-muted-foreground" />
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="winRate">Win Rate</SelectItem>
              <SelectItem value="totalGames">Most Played</SelectItem>
              <SelectItem value="name">Name A-Z</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {filtered.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <p className="text-sm text-muted-foreground">
            No openings found matching your search.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((stat) => (
            <OpeningCard key={`${stat.epd}-${stat.color}`} stat={stat} />
          ))}
        </div>
      )}

      <p className="text-center text-xs text-muted-foreground">
        Showing {filtered.length} of {stats.length} openings
      </p>
    </div>
  )
}
