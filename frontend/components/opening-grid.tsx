"use client"

import { useEffect, useRef, useState } from "react"
import { useTranslations } from "next-intl"
import { OpeningCard } from "@/components/opening-card"
import type { OpeningStatView } from "@/lib/types"

interface OpeningGridProps {
  stats: OpeningStatView[] // 이미 필터링되고 정렬된 데이터
}

const ITEMS_PER_PAGE = 20

export function OpeningGrid({ stats }: OpeningGridProps) {
  const t = useTranslations("opening")
  const [visibleCount, setVisibleCount] = useState(ITEMS_PER_PAGE)
  const sentinelRef = useRef<HTMLDivElement | null>(null)

  // stats prop이 변경될 때 visibleCount 초기화
  useEffect(() => {
    setVisibleCount(ITEMS_PER_PAGE)
  }, [stats])

  useEffect(() => {
    const node = sentinelRef.current
    if (!node) {
      return
    }

    if (typeof window === "undefined" || !("IntersectionObserver" in window)) {
      setVisibleCount(stats.length)
      return
    }

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) {
          setVisibleCount((prev) => Math.min(prev + ITEMS_PER_PAGE, stats.length))
        }
      },
      { rootMargin: "220px" }
    )

    observer.observe(node)

    return () => observer.disconnect()
  }, [stats.length])

  const visibleItems = stats.slice(0, visibleCount)

  return (
    <div className="flex flex-col gap-6">
      {/* 결과 영역 */}
      {visibleItems.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <p className="text-sm text-muted-foreground">
            {t("noOpeningsFoundSearch")}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {visibleItems.map((stat) => (
            <OpeningCard key={`${stat.epd}-${stat.color}`} stat={stat} />
          ))}
        </div>
      )}

      <p className="text-center text-xs text-muted-foreground">
        {t("showingResults", { visible: visibleItems.length, total: stats.length })}
      </p>

      {visibleItems.length < stats.length && (
        <div ref={sentinelRef} className="h-1 w-full" aria-hidden />
      )}
    </div>
  )
}
