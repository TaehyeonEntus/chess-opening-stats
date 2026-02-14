"use client"

import { useState, useEffect } from "react"
import { OpeningGrid } from "@/components/opening-grid"
import { Skeleton } from "@/components/ui/skeleton"
import { provideAllOpeningResult } from "@/lib/provide/provideFacade"
import {OpeningStatView} from "@/lib/types";

export default function HomePage() {
  const [openings, setOpenings] = useState<OpeningStatView[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function load() {
      setLoading(true)
      const data = await provideAllOpeningResult()
      setOpenings(data)
      setLoading(false)
    }

    load()
  }, [])

  return (
      <div className="min-h-screen bg-background">
        <main className="mx-auto max-w-7xl px-4 py-6 lg:px-6">
          {loading ? (
              <LoadingSkeleton />
          ) : (
              <OpeningGrid stats={openings} />
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
