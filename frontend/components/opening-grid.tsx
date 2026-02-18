"use client"

import { useState, useMemo, useEffect, useCallback } from "react"
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
import type { OpeningStatView, ColorFilter, SortBy } from "@/lib/types"
import { Search, ArrowDownWideNarrow } from "lucide-react"

interface OpeningGridProps {
  stats: OpeningStatView[]
}

const ITEMS_PER_PAGE = 20;

export function OpeningGrid({ stats }: OpeningGridProps) {
  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames")
  const [search, setSearch] = useState("")
  const [visibleCount, setVisibleCount] = useState(ITEMS_PER_PAGE)

  const filtered = useMemo(() => {
    let list = [...stats]

    // 색상 필터
    if (colorFilter !== "all") {
      list = list.filter((s) => s.color === colorFilter)
    }

    // 검색 필터
    if (search.trim()) {
      const q = search.toLowerCase()
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
  }, [stats, colorFilter, search, sortBy])

  // 필터나 정렬 조건이 바뀌면 보여주는 개수 초기화
  useEffect(() => {
    setVisibleCount(ITEMS_PER_PAGE);
  }, [colorFilter, search, sortBy]);

  const handleScroll = useCallback(() => {
    if (window.innerHeight + document.documentElement.scrollTop < document.documentElement.offsetHeight - 50) {
      return;
    }
    setVisibleCount(prev => Math.min(prev + ITEMS_PER_PAGE, filtered.length));
  }, [filtered.length]);

  useEffect(() => {
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [handleScroll]);

  const visibleItems = filtered.slice(0, visibleCount);

  return (
      <div className="flex flex-col gap-6">
        {/* 필터 영역 */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <Tabs
              value={colorFilter}
              onValueChange={(v) => setColorFilter(v as ColorFilter)}
          >
            <TabsList>
              <TabsTrigger value="all">All</TabsTrigger>
              <TabsTrigger value="white">White</TabsTrigger>
              <TabsTrigger value="black">Black</TabsTrigger>
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

        {/* 결과 영역 */}
        {visibleItems.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <p className="text-sm text-muted-foreground">
                No openings found matching your search.
              </p>
            </div>
        ) : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
              {visibleItems.map((stat) => (
                  <OpeningCard key={`${stat.epd}-${stat.color}`} stat={stat} />
              ))}
            </div>
        )}

        <p className="text-center text-xs text-muted-foreground">
          Showing {visibleItems.length} of {filtered.length} openings
        </p>
      </div>
  )
}
