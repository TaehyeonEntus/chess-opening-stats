"use client"

import { useEffect, useRef, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { WinRateBar } from "@/components/win-rate-bar"
import { Chessboard } from "react-chessboard"
import type { OpeningStatView } from "@/lib/types"

interface OpeningCardProps {
  stat: OpeningStatView
}

const boardVisibilityCallbacks = new Map<Element, () => void>()
let boardVisibilityObserver: IntersectionObserver | null = null

function getBoardVisibilityObserver() {
  if (boardVisibilityObserver || typeof window === "undefined" || !("IntersectionObserver" in window)) {
    return boardVisibilityObserver
  }

  boardVisibilityObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) {
          return
        }

        const reveal = boardVisibilityCallbacks.get(entry.target)
        if (reveal) {
          reveal()
          boardVisibilityCallbacks.delete(entry.target)
          boardVisibilityObserver?.unobserve(entry.target)
        }
      })
    },
    { rootMargin: "200px" }
  )

  return boardVisibilityObserver
}

export function OpeningCard({ stat }: OpeningCardProps) {
  const boardRef = useRef<HTMLDivElement | null>(null)
  const [isBoardVisible, setIsBoardVisible] = useState(false)

  useEffect(() => {
    const node = boardRef.current
    if (!node) {
      return
    }

    if (typeof window === "undefined" || !("IntersectionObserver" in window)) {
      setIsBoardVisible(true)
      return
    }

    const observer = getBoardVisibilityObserver()
    if (!observer) {
      setIsBoardVisible(true)
      return
    }

    boardVisibilityCallbacks.set(node, () => setIsBoardVisible(true))
    observer.observe(node)

    return () => {
      boardVisibilityCallbacks.delete(node)
      observer.unobserve(node)
    }
  }, [])

  return (
    <Card className="group transition-colors hover:border-foreground/20">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between gap-2">
          <div className="min-w-0 flex-1">
            <CardTitle className="truncate text-sm font-semibold leading-tight">
              {stat.name}
            </CardTitle>
            <div className="mt-1.5 flex items-center gap-2">
              <Badge variant="outline" className="font-mono text-xs">
                {stat.eco}
              </Badge>
              <span
                className={`inline-flex h-4 w-4 items-center justify-center rounded-full border ${
                  stat.color === "white"
                    ? "border-foreground/20 bg-white"
                    : "border-foreground/20 bg-black"
                }`}
                aria-label={stat.color === "white" ? "White pieces" : "Black pieces"}
              />
            </div>
          </div>
          <div className="text-right">
            <div className="flex items-start justify-end gap-4">
              <div className="text-right">
                <p className="text-2xl font-bold tabular-nums text-foreground">
                  {stat.winRate}%
                </p>
                <p className="text-xs text-muted-foreground">win rate</p>
              </div>
              <div className="text-right">
                <p className="text-2xl font-bold tabular-nums text-foreground">
                  {stat.totalGames}
                </p>
                <p className="text-xs text-muted-foreground">games</p>
              </div>
            </div>
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        <div className="mb-4 flex justify-center">
          <div
            ref={boardRef}
            className="pointer-events-none aspect-square w-full max-w-[200px]"
          >
            {isBoardVisible ? (
              <Chessboard
                options={{
                  position: stat.epd,
                  allowDragging: false,
                  showNotation: false,
                  boardOrientation: stat.color,
                  darkSquareStyle: { backgroundColor: "#779954" },
                  lightSquareStyle: { backgroundColor: "#e9edcc" },
                }}
              />
            ) : (
              <div
                className="h-full w-full rounded-md bg-muted/40"
                aria-hidden
              />
            )}
          </div>
        </div>
        <WinRateBar winRate={stat.winRate} drawRate={stat.drawRate} lossRate={stat.lossRate} />
        <div className="mt-3 flex justify-between text-xs tabular-nums text-muted-foreground">
          <span className="flex items-center gap-1">
            <span className="inline-block h-2 w-2 rounded-full bg-emerald-500" />
            {stat.wins}W
          </span>
          <span className="flex items-center gap-1">
            <span className="inline-block h-2 w-2 rounded-full bg-amber-400" />
            {stat.drawRate > 0 ? `${stat.draws}D` : "0D"}
          </span>
          <span className="flex items-center gap-1">
            <span className="inline-block h-2 w-2 rounded-full bg-rose-500" />
            {stat.losses}L
          </span>
        </div>
      </CardContent>
    </Card>
  )
}
