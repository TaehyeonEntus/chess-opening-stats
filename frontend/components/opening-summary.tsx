"use client"

import { useTranslations } from "next-intl"
import { Card, CardContent } from "@/components/ui/card"
import type { DisplaySummary, ColorFilter, OpeningStatView } from "@/lib/types"
import { Chessboard } from "react-chessboard"
import { Badge } from "@/components/ui/badge"

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
    <section className="flex h-full min-w-0 flex-col gap-6 p-6">
      <h3 className="text-lg font-bold tracking-tight">{title}</h3>

      <div className="flex flex-col gap-1">
        <span className="text-xs font-semibold uppercase text-muted-foreground tracking-wider">{t("totalGames")}</span>
        <span className="text-4xl font-extrabold tabular-nums tracking-tighter">{totalGames.toLocaleString()}</span>
      </div>

      <div className="space-y-4">
        <span className="text-xs font-semibold uppercase text-muted-foreground tracking-wider">{t("resultsDistribution")}</span>
        <div className="space-y-3 text-sm">
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
  const otherOpenings = openings.slice(1, 4)

  if (!topOpening) {
    return (
      <section className="flex h-full min-w-0 flex-col p-6">
        <div className="flex items-start justify-between gap-3 overflow-hidden">
          <h3 className="text-lg font-bold tracking-tight truncate">{title}</h3>
        </div>
        <div className="mt-8 flex flex-col items-center justify-center py-8 rounded-lg border border-dashed border-muted-foreground/20">
          <p className="text-sm text-muted-foreground">{t("noDataAvailable")}</p>
        </div>
      </section>
    )
  }

  return (
    <section className="flex h-full min-w-0 flex-col gap-6 p-6 overflow-hidden">
      <div className="flex items-start justify-between gap-3 overflow-hidden">
        <h3 className="text-lg font-bold tracking-tight truncate">{title}</h3>
      </div>

      <div className="flex gap-4 min-w-0 overflow-hidden items-center p-3 rounded-xl bg-muted/30 border border-muted-foreground/10">
        <div className="pointer-events-none aspect-square w-20 flex-none overflow-hidden rounded-lg border shadow-sm sm:w-24" aria-hidden="true">
          <Chessboard
            key={topOpening.epd}
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
        <div className="flex min-w-0 flex-1 flex-col justify-center gap-1">
          <div className="truncate font-bold leading-tight text-sm sm:text-base" title={topOpening.name}>
            {topOpening.name}
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <Badge variant="secondary" className="font-mono text-[10px] h-4 px-1 leading-none">
              {topOpening.eco}
            </Badge>
            <div className="flex items-center gap-1.5 text-[11px] font-medium text-muted-foreground">
              <span className="tabular-nums">{topOpening.totalGames.toLocaleString()} {t("games")}</span>
              <span className="text-emerald-600 dark:text-emerald-500 font-bold tabular-nums">{topOpening.winRate}%</span>
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
                <div className="truncate font-semibold group-hover:text-primary transition-colors">{op.name}</div>
                <div className="flex items-center gap-2 text-[10px] text-muted-foreground font-medium">
                  <span className="font-mono">{op.eco}</span>
                  <span className="tabular-nums">{op.totalGames.toLocaleString()} {t("games")}</span>
                </div>
              </div>
              <div className="flex-none text-right">
                <div className="font-bold text-emerald-600 dark:text-emerald-500 tabular-nums">{op.winRate}%</div>
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
