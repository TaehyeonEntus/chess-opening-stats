"use client"

import { useState, useEffect } from "react"
import { Header } from "@/components/header"
import { OpeningGrid } from "@/components/opening-grid"
import { BoardExplorer } from "@/components/board-explorer"
import { Skeleton } from "@/components/ui/skeleton"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import type { Opening, OpeningResult } from "@/lib/types"
import { fetchOpeningMaster, fetchOpeningResults } from "@/lib/api"
import { BarChart3, SquareMousePointer } from "lucide-react"

type ViewMode = "stats" | "board"

export default function HomePage() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [openings, setOpenings] = useState<Opening[]>([])
  const [results, setResults] = useState<OpeningResult[]>([])
  const [loading, setLoading] = useState(true)
  const [viewMode, setViewMode] = useState<ViewMode>("stats")

  useEffect(() => {
    async function loadData() {
      setLoading(true)
      const [masterData, resultData] = await Promise.all([
        fetchOpeningMaster(),
        fetchOpeningResults(isLoggedIn ? "demo-player" : undefined),
      ])
      setOpenings(masterData)
      setResults(resultData)
      setLoading(false)
    }
    loadData()
  }, [isLoggedIn])

  return (
    <div className="min-h-screen bg-background">
      <Header
        isLoggedIn={isLoggedIn}
        playerName={isLoggedIn ? "DemoPlayer" : undefined}
        onToggleLogin={() => setIsLoggedIn((prev) => !prev)}
      />
      <main className="mx-auto max-w-7xl px-4 py-6 lg:px-6">
        <div className="mb-6 flex items-center justify-between">
          <Tabs
            value={viewMode}
            onValueChange={(v) => setViewMode(v as ViewMode)}
          >
            <TabsList>
              <TabsTrigger value="stats" className="gap-1.5">
                <BarChart3 className="h-4 w-4" />
                Opening Stats
              </TabsTrigger>
              <TabsTrigger value="board" className="gap-1.5">
                <SquareMousePointer className="h-4 w-4" />
                Board Explorer
              </TabsTrigger>
            </TabsList>
          </Tabs>
        </div>

        {loading ? (
          <LoadingSkeleton />
        ) : viewMode === "stats" ? (
          <OpeningGrid
            openings={openings}
            results={results}
            isLoggedIn={isLoggedIn}
          />
        ) : (
          <BoardExplorer openings={openings} results={results} />
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
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 9 }).map((_, i) => (
          <Skeleton key={i} className="h-32 rounded-lg" />
        ))}
      </div>
    </div>
  )
}
