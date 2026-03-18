"use client"

import { useTranslations } from "next-intl"
import { Card, CardContent } from "@/components/ui/card"
import type { DisplaySummary, ColorFilter, Stat } from "@/lib/types"
import { calculateRatesFromCounts } from "@/lib/stats"
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
    <Card className="relative overflow-hidden">
      <div className={`absolute right-0 top-0 h-1 w-full ${colorIndicator}`} />
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
      <h3 className="text-lg font-semibold">{title}</h3>

      <div className="flex flex-col gap-1">
        <span className="text-sm text-muted-foreground">{t("totalGames")}</span>
        <span className="text-3xl font-semibold tabular-nums">{totalGames.toLocaleString()}</span>
      </div>

      <div className="space-y-3">
        <span className="text-sm font-medium text-muted-foreground">{t("resultsDistribution")}</span>
        <div className="space-y-2 text-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-emerald-500" />
              <span>{t("wins")}</span>
            </div>
            <span className="tabular-nums">
              {totalWins.toLocaleString()} ({hasData ? winRate : 0}%)
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-amber-400" />
              <span>{t("draws")}</span>
            </div>
            <span className="tabular-nums">
              {totalDraws.toLocaleString()} ({hasData ? drawRate : 0}%)
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-rose-500" />
              <span>{t("losses")}</span>
            </div>
            <span className="tabular-nums">
              {totalLosses.toLocaleString()} ({hasData ? lossRate : 0}%)
            </span>
          </div>
        </div>
      </div>
    </section>
  )
}

interface FeaturedOpeningSectionProps {
  title: string
  openings: Stat[]
}

function FeaturedOpeningSection({ title, openings }: FeaturedOpeningSectionProps) {
  const t = useTranslations("opening")
  const topOpening = openings[0]
  const otherOpenings = openings.slice(1, 3)

  if (!topOpening) {
    return (
      <section className="flex h-full min-w-0 flex-col p-6">
        <div className="flex items-start justify-between gap-3 overflow-hidden">
          <h3 className="text-lg font-semibold truncate">{title}</h3>
          {title === t("bestWinRate") && (
            <p className="flex-none max-w-[10rem] text-right text-xs text-muted-foreground whitespace-nowrap">
              {t("bestWinRateNote")}
            </p>
          )}
        </div>
        <div className="mt-6 text-sm text-muted-foreground">{t("noDataAvailable")}</div>
      </section>
    )
  }

  const topOpeningRates = calculateRatesFromCounts(topOpening.wins, topOpening.draws, topOpening.losses)

  return (
    <section className="flex h-full min-w-0 flex-col gap-6 p-6 overflow-hidden">
      <div className="flex items-start justify-between gap-3 overflow-hidden min-h-[3.5rem]">
        <h3 className="text-lg font-semibold truncate">{title}</h3>
        {title === t("bestWinRate") && (
          <p className="flex-none max-w-[10rem] text-right text-xs text-muted-foreground">
            {t("bestWinRateNote")}
          </p>
        )}
      </div>

      <div className="flex gap-4 min-w-0 overflow-hidden min-h-[6rem]">
        <div className="pointer-events-none aspect-square w-20 flex-none overflow-hidden rounded-md border bg-muted/50 sm:w-24" aria-hidden="true">
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
        <div className="flex min-w-0 flex-1 flex-col justify-center py-0.5">
          <div className="space-y-1 overflow-hidden">
            <div className="truncate font-semibold leading-tight text-sm sm:text-base" title={topOpening.name}>
              {topOpening.name}
            </div>
            <div className="flex items-center gap-2 text-xs text-muted-foreground">
              <Badge variant="outline" className="font-mono text-[10px] h-4 px-1">
                {topOpening.eco}
              </Badge>
              <span
                className={`inline-block h-2.5 w-2.5 rounded-full border ${
                  topOpening.color === "white" ? "bg-white border-gray-300" : "bg-black border-gray-700"
                }`}
                title={topOpening.color}
              />
              <span className="whitespace-nowrap">{topOpeningRates.totalGames.toLocaleString()} {t("games")}</span>
            </div>
          </div>
          <div className="mt-2 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs tabular-nums">
            <span className="font-medium text-foreground whitespace-nowrap">{topOpeningRates.winRate}% {t("winRate")}</span>
            <div className="flex items-center gap-2">
              <span className="text-emerald-600">{topOpening.wins}W</span>
              <span className="text-amber-500">{topOpening.draws}D</span>
              <span className="text-rose-500">{topOpening.losses}L</span>
            </div>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-2 min-w-0 overflow-hidden">
        <h4 className="text-[10px] font-semibold uppercase text-muted-foreground tracking-wider">{t("runnerUps")}</h4>
        <div className="flex flex-col gap-1.5 overflow-hidden">
          {otherOpenings.map((op, index) => {
            const opRates = calculateRatesFromCounts(op.wins, op.draws, op.losses)
            return (
              <div
                key={`${op.name}-${op.color}-${index}`}
                className="flex items-center justify-between rounded-md border p-2 text-xs transition-colors hover:bg-muted/50 min-w-0 overflow-hidden"
              >
                <div className="flex min-w-0 flex-1 flex-col gap-0.5 pr-2 overflow-hidden">
                  <div className="truncate font-medium leading-tight">{op.name}</div>
                  <div className="flex items-center gap-2 text-[10px] text-muted-foreground">
                    <span className="font-mono">{op.eco}</span>
                    <span
                      className={`inline-block h-2 w-2 rounded-full border ${
                        op.color === "white" ? "bg-white border-gray-300" : "bg-black border-gray-700"
                      }`}
                      title={op.color}
                    />
                    <span className="whitespace-nowrap">{opRates.totalGames.toLocaleString()} {t("games")}</span>
                  </div>
                </div>
                <div className="flex-none text-right text-[10px] tabular-nums">
                  <div className="font-semibold text-foreground">{opRates.winRate}%</div>
                </div>
              </div>
            )
          })}
          {otherOpenings.length === 0 && (
            <div className="text-xs text-muted-foreground italic">{t("noOtherOpenings")}</div>
          )}
        </div>
      </div>
    </section>
  )
}
