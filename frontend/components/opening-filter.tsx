"use client"

import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import type { ColorFilter, SortBy } from "@/lib/types"
import { Search, ArrowDownWideNarrow } from "lucide-react"

interface OpeningFilterProps {
  colorFilter: ColorFilter
  onColorFilterChange: (filter: ColorFilter) => void
  sortBy: SortBy
  onSortByChange: (sortBy: SortBy) => void
  search: string
  onSearchChange: (search: string) => void
}

export function OpeningFilter({
  colorFilter,
  onColorFilterChange,
  sortBy,
  onSortByChange,
  search,
  onSearchChange,
}: OpeningFilterProps) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <Tabs
        value={colorFilter}
        onValueChange={(v) => onColorFilterChange(v as ColorFilter)}
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
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-9"
          />
        </div>

        <Select value={sortBy} onValueChange={(v) => onSortByChange(v as SortBy)}>
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
  )
}
