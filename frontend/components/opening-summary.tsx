"use client"

import { useTranslations } from "next-intl"
import { Card, CardContent } from "@/components/ui/card"
import type { DisplaySummary, ColorFilter, OpeningStatView } from "@/lib/types"
import { Chessboard } from "react-chessboard"
import { Badge } from "@/components/ui/badge"
import { cn, getWinRateColor } from "@/lib/utils"

interface OpeningSummaryProps {
  summary: DisplaySummary
  colorFilter: ColorFilter
}

export function OpeningSummary({ summary, colorFilter }: OpeningSummaryProps) {
  const t = useTranslations("opening")
  const {
    totalGames,
    totalWins,
    totalDraws,
    totalLosses,
    winRate,
    drawRate,
    lossRate,
    bestWinRateOpenings,
    mostPlayedOpenings,
  } = summary

  const colorIndicator =
    colorFilter === "white"
      ? "bg-white text-black"
      : colorFilter === "black"
      ? "bg-zinc-900 text-white"
      : "bg-gradient-to-r from-white via-zinc-400 to-zinc-900 text-black"

  const summaryTitle =
    colorFilter === "white"
      ? t("whiteStats")
      : colorFilter === "black"
      ? t("blackStats")
      : t("overallStats")

  return (
    <Card className="relative overflow-hidden border shadow-sm">
      <div className={`absolute right-0 top-0 h-1 w-full ${colorIndicator}`} aria-hidden="true" />
      <CardContent className="p-0">
        <div className="grid grid-cols-1 divide-y lg:grid-cols-3 lg:divide-x lg:divide-y-0">
          <SummaryOverallSection
            title={summaryTitle}
            totalGames={totalGames}
            totalWins={totalWins}
            totalDraws={totalDraws}
            totalLosses={totalLosses}
            winRate={winRate}
            drawRate={drawRate}
            lossRate={lossRate}
          />

          <FeaturedOpeningSection title={t("bestWinRate")} openings={bestWinRateOpenings} />

          <FeaturedOpeningSection title={t("mostPlayed")} openings={mostPlayedOpenings} />
        </div>
      </CardContent>
    </Card>
  )
}

interface SummaryOverallSectionProps {
  title: string
  totalGames: number
  totalWins: number
  totalDraws: number
  totalLosses: number
  winRate: number
  drawRate: number
  lossRate: number
}

function SummaryOverallSection({
  title,
  totalGames,
  totalWins,
  totalDraws,
  totalLosses,
  winRate,
  drawRate,
  lossRate,
}: SummaryOverallSectionProps) {
  const t = useTranslations("opening")
  const hasData = totalGames > 0

  return (
    <section className="flex h-full min-w-0 flex-col gap-3 p-6">
      <h3 className="text-lg font-bold tracking-tight">{title}</h3>

      <div className="flex flex-col gap-1">
        <span className="text-xs font-semibold uppercase text-muted-foreground tracking-wider">{t("totalGames")}</span>
        <span className="text-5xl font-extrabold tabular-nums tracking-tighter">{totalGames.toLocaleString()}</span>
      </div>

      <div className="space-y-3 mt-auto">
        <span className="text-xs font-semibold uppercase text-muted-foreground tracking-wider">{t("resultsDistribution")}</span>
        <div className="space-y-4 text-base">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]" />
              <span className="font-medium">{t("wins")}</span>
            </div>
            <span className="tabular-nums font-semibold">
              {totalWins.toLocaleString()} <span className="text-muted-foreground font-normal ml-1">({hasData ? winRate : 0}%)</span>
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-amber-400 shadow-[0_0_8px_rgba(251,191,36,0.5)]" />
              <span className="font-medium">{t("draws")}</span>
            </div>
            <span className="tabular-nums font-semibold">
              {totalDraws.toLocaleString()} <span className="text-muted-foreground font-normal ml-1">({hasData ? drawRate : 0}%)</span>
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.5)]" />
              <span className="font-medium">{t("losses")}</span>
            </div>
            <span className="tabular-nums font-semibold">
              {totalLosses.toLocaleString()} <span className="text-muted-foreground font-normal ml-1">({hasData ? lossRate : 0}%)</span>
            </span>
          </div>
        </div>
      </div>
    </section>
  )
}

interface FeaturedOpeningSectionProps {
  title: string
  openings: OpeningStatView[]
}

function FeaturedOpeningSection({ title, openings }: FeaturedOpeningSectionProps) {
  const t = useTranslations("opening")
  const topOpening = openings[0]
  const otherOpenings = openings.slice(1, 3)

  if (!topOpening) {
    return (
      <section className="flex h-full min-w-0 flex-col p-6">
        <div className="flex items-start justify-between gap-3 overflow-hidden">
          <h3 className="text-lg font-bold tracking-tight truncate">{title}</h3>
        </div>
        <div className="mt-4 flex flex-col items-center justify-center py-8 rounded-lg border border-dashed border-muted-foreground/20">
          <p className="text-sm text-muted-foreground">{t("noDataAvailable")}</p>
        </div>
      </section>
    )
  }

  const isBestWinRate = title === t("bestWinRate")

  return (
    <section className="flex h-full min-w-0 flex-col gap-4 p-6 overflow-hidden">
      <div className="flex items-baseline gap-2 overflow-hidden">
        <h3 className="text-lg font-bold tracking-tight truncate">{title}</h3>
        {isBestWinRate && (
          <p className="text-xs text-muted-foreground leading-tight shrink-0">
            {t("bestWinRateNote")}
          </p>
        )}
      </div>

      <div className="flex gap-4 min-w-0 overflow-hidden items-start p-3 rounded-xl bg-muted/30 border border-muted-foreground/10">
        <div className="pointer-events-none aspect-square w-16 flex-none overflow-hidden rounded-lg border shadow-sm sm:w-24 mt-1" aria-hidden="true">
          <Chessboard
            options={{
              position: topOpening.epd,
              boardOrientation: topOpening.color,
              allowDragging: false,
              showNotation: false,
              darkSquareStyle: { backgroundColor: "#779954" },
              lightSquareStyle: { backgroundColor: "#e9edcc" },
            }}
          />
        </div>
        <div className="flex min-w-0 flex-1 flex-col justify-between gap-3 self-stretch">
          <div className="flex justify-between items-start gap-2">
            <div className="flex flex-col gap-1 min-w-0">
              <div className="truncate font-bold leading-tight text-sm sm:text-base" title={topOpening.name}>
                {topOpening.name}
              </div>
              <Badge variant="secondary" className="w-fit font-mono text-xs h-5 px-1.5 leading-none shrink-0">
                {topOpening.eco}
              </Badge>
            </div>
            
            <div className="flex flex-col items-end shrink-0 pl-1">
              <div className={cn("font-bold tabular-nums text-xl", getWinRateColor(topOpening.winRate, "text"))}>
                {topOpening.winRate}%
              </div>
            </div>
          </div>

          <div className="flex items-end justify-between mt-auto w-full">
            <div className="flex items-center gap-3 text-xs font-medium">
              <div className="flex items-center gap-1" title={t("wins")}>
                <div className="h-2 w-2 rounded-full bg-emerald-500" />
                <span className="tabular-nums text-emerald-600 dark:text-emerald-400 text-sm">{topOpening.wins}W</span>
              </div>
              <div className="flex items-center gap-1" title={t("draws")}>
                <div className="h-2 w-2 rounded-full bg-amber-400" />
                <span className="tabular-nums text-amber-600 dark:text-amber-400 text-sm">{topOpening.draws}D</span>
              </div>
              <div className="flex items-center gap-1" title={t("losses")}>
                <div className="h-2 w-2 rounded-full bg-rose-500" />
                <span className="tabular-nums text-rose-600 dark:text-rose-400 text-sm">{topOpening.losses}L</span>
              </div>
            </div>
            <div className="tabular-nums text-sm text-muted-foreground">
              {topOpening.totalGames.toLocaleString()} {t("games")}
            </div>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-3 min-w-0 overflow-hidden">
        <h4 className="text-[10px] font-bold uppercase text-muted-foreground tracking-widest">{t("runnerUps")}</h4>
        <div className="grid gap-2">
          {otherOpenings.map((op, index) => (
            <div
              key={`${op.name}-${op.color}-${index}`}
              className="flex items-center justify-between rounded-lg border bg-card/50 px-3 py-2 text-xs transition-all hover:bg-muted/50 min-w-0 group"
            >
              <div className="flex min-w-0 flex-1 flex-col gap-0.5 pr-3">
                <div className="truncate font-semibold group-hover:text-primary transition-colors" title={op.name}>{op.name}</div>
                <div className="text-[10px] text-muted-foreground font-medium font-mono">{op.eco}</div>
              </div>
              <div className="flex flex-col items-end gap-0.5 text-[10px] tabular-nums font-medium">
                <div className="flex items-center gap-2 mb-0.5">
                  <span className={cn("text-xs font-bold", getWinRateColor(op.winRate, "text"))}>{op.winRate}%</span>
                  <span className="text-muted-foreground">{op.totalGames.toLocaleString()} {t("games")}</span>
                </div>
                <div className="flex gap-2">
                  <span className="text-emerald-600 dark:text-emerald-400">{op.wins}W</span>
                  <span className="text-amber-600 dark:text-amber-400">{op.draws}D</span>
                  <span className="text-rose-600 dark:text-rose-400">{op.losses}L</span>
                </div>
              </div>
            </div>
          ))}
          {otherOpenings.length === 0 && (
            <div className="text-xs text-muted-foreground italic px-2">{t("noOtherOpenings")}</div>
          )}
        </div>
      </div>
    </section>
  )
}

