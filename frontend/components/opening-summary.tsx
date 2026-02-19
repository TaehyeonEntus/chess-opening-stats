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

  return (
    <Card className="relative overflow-hidden">
      <div className={`absolute right-0 top-0 h-1 w-full ${colorIndicator}`} />
      <CardContent className="p-0">
        <div className="grid grid-cols-1 divide-y lg:grid-cols-3 lg:divide-x lg:divide-y-0">
          <SummaryOverallSection
            totalGames={totalGames}
            totalWins={totalWins}
            totalDraws={totalDraws}
            totalLosses={totalLosses}
            winRate={winRate}
            drawRate={drawRate}
            lossRate={lossRate}
          />

          <FeaturedOpeningSection title="Best Win Rate" openings={bestWinRateOpenings} />

          <FeaturedOpeningSection title="Most Played" openings={mostPlayedOpenings} />
        </div>
      </CardContent>
    </Card>
  )
}

interface SummaryOverallSectionProps {
  totalGames: number
  totalWins: number
  totalDraws: number
  totalLosses: number
  winRate: number
  drawRate: number
  lossRate: number
}

function SummaryOverallSection({
  totalGames,
  totalWins,
  totalDraws,
  totalLosses,
  winRate,
  drawRate,
  lossRate,
}: SummaryOverallSectionProps) {
  return (
    <section className="flex h-full min-w-0 flex-col gap-6 p-6">
      <h3 className="text-lg font-semibold">Overall Stats</h3>

      <div className="flex flex-col gap-1">
        <span className="text-sm text-muted-foreground">Total Games</span>
        <span className="text-3xl font-semibold tabular-nums">{totalGames.toLocaleString()}</span>
      </div>

      <div className="space-y-3">
        <span className="text-sm font-medium text-muted-foreground">Results Distribution</span>
        <div className="space-y-2 text-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-emerald-500" />
              <span>Wins</span>
            </div>
            <span className="tabular-nums">
              {totalWins.toLocaleString()} ({winRate}%)
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-amber-400" />
              <span>Draws</span>
            </div>
            <span className="tabular-nums">
              {totalDraws.toLocaleString()} ({drawRate}%)
            </span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-rose-500" />
              <span>Losses</span>
            </div>
            <span className="tabular-nums">
              {totalLosses.toLocaleString()} ({lossRate}%)
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
  const topOpening = openings[0]
  const otherOpenings = openings.slice(1, 3)

  if (!topOpening) {
    return (
      <section className="flex h-full min-w-0 flex-col p-6">
        <div className="flex items-start justify-between gap-3">
          <h3 className="text-lg font-semibold">{title}</h3>
          {title === "Best Win Rate" && (
            <p className="max-w-[10rem] text-right text-xs text-muted-foreground">
              Based on openings with at least 30 games.
            </p>
          )}
        </div>
        <div className="mt-6 text-sm text-muted-foreground">No data available</div>
      </section>
    )
  }

  const topOpeningRates = calculateRatesFromCounts(topOpening.wins, topOpening.draws, topOpening.losses)

  return (
    <section className="flex h-full min-w-0 flex-col gap-6 p-6">
      <div className="flex items-start justify-between gap-3">
        <h3 className="text-lg font-semibold">{title}</h3>
        {title === "Best Win Rate" && (
          <p className="max-w-[10rem] text-right text-xs text-muted-foreground">
            Based on openings with at least 30 games.
          </p>
        )}
      </div>

      <div className="flex gap-4">
        <div className="pointer-events-none aspect-square w-24 flex-none overflow-hidden rounded-md border bg-muted/50 sm:w-28">
          <Chessboard
            key={topOpening.epd}
            options={{
              position: topOpening.epd,
              allowDragging: false,
              showNotation: false,
              boardOrientation: topOpening.color,
              darkSquareStyle: { backgroundColor: "#779954" },
              lightSquareStyle: { backgroundColor: "#e9edcc" },
            }}
          />
        </div>
        <div className="flex min-w-0 flex-1 flex-col justify-between py-0.5">
          <div className="space-y-1">
            <div className="truncate font-semibold leading-tight" title={topOpening.name}>
              {topOpening.name}
            </div>
            <div className="flex items-center gap-2 text-xs text-muted-foreground">
              <Badge variant="outline" className="font-mono">
                {topOpening.eco}
              </Badge>
              <span
                className={`inline-block h-3 w-3 rounded-full border ${
                  topOpening.color === "white" ? "bg-white border-gray-300" : "bg-black border-gray-700"
                }`}
                title={topOpening.color}
              />
              <span>{topOpeningRates.totalGames.toLocaleString()} games</span>
            </div>
          </div>
          <div className="flex items-center gap-3 text-xs tabular-nums">
            <span className="font-medium text-foreground">{topOpeningRates.winRate}% WR</span>
            <span className="text-emerald-600">{topOpening.wins}W</span>
            <span className="text-amber-500">{topOpening.draws}D</span>
            <span className="text-rose-500">{topOpening.losses}L</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-3">
        <h4 className="text-xs font-semibold uppercase text-muted-foreground">Runner-ups</h4>
        <div className="grid gap-2">
          {otherOpenings.map((op, index) => {
            const opRates = calculateRatesFromCounts(op.wins, op.draws, op.losses)
            return (
              <div
                key={`${op.epd}-${op.color}-${index}`}
                className="flex items-center justify-between rounded-md border p-2 text-sm transition-colors hover:bg-muted/50"
              >
                <div className="flex min-w-0 flex-1 flex-col gap-0.5 pr-3">
                  <div className="truncate font-medium leading-tight">{op.name}</div>
                  <div className="flex items-center gap-2 text-xs text-muted-foreground">
                    <span className="font-mono text-[10px]">{op.eco}</span>
                    <span
                      className={`inline-block h-2.5 w-2.5 rounded-full border ${
                        op.color === "white" ? "bg-white border-gray-300" : "bg-black border-gray-700"
                      }`}
                      title={op.color}
                    />
                    <span>{opRates.totalGames.toLocaleString()} games</span>
                  </div>
                </div>
                <div className="text-right text-xs tabular-nums">
                  <div className="font-medium">{opRates.winRate}% WR</div>
                </div>
              </div>
            )
          })}
          {otherOpenings.length === 0 && (
            <div className="text-sm text-muted-foreground">No other openings</div>
          )}
        </div>
      </div>
    </section>
  )
}
