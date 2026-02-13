import { Card, CardContent } from "@/components/ui/card"
import { WinRateBar } from "@/components/win-rate-bar"
import type { SummaryStats } from "@/lib/types"
import { Trophy, TrendingUp, Swords, BarChart3 } from "lucide-react"

interface StatsSummaryProps {
  summary: SummaryStats
  isLoggedIn: boolean
}

export function StatsSummary({ summary, isLoggedIn }: StatsSummaryProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center gap-2">
        <h2 className="text-lg font-semibold text-foreground">
          {isLoggedIn ? "Your Stats" : "Global Stats"}
        </h2>
        {!isLoggedIn && (
          <span className="text-xs text-muted-foreground">All players</span>
        )}
      </div>
      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        <Card>
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-foreground/5">
              <Swords className="h-5 w-5 text-foreground/70" />
            </div>
            <div className="min-w-0">
              <p className="text-2xl font-bold tabular-nums text-foreground">
                {summary.totalGames.toLocaleString()}
              </p>
              <p className="truncate text-xs text-muted-foreground">Total Games</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-emerald-500/10">
              <BarChart3 className="h-5 w-5 text-emerald-500" />
            </div>
            <div className="min-w-0">
              <p className="text-2xl font-bold tabular-nums text-emerald-500">
                {summary.overallWinRate}%
              </p>
              <p className="truncate text-xs text-muted-foreground">Win Rate</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-foreground/5">
              <TrendingUp className="h-5 w-5 text-foreground/70" />
            </div>
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold text-foreground">
                {summary.mostPlayedOpening}
              </p>
              <p className="truncate text-xs text-muted-foreground">Most Played</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="flex items-center gap-3 p-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-amber-400/10">
              <Trophy className="h-5 w-5 text-amber-500" />
            </div>
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold text-foreground">
                {summary.bestOpening}
              </p>
              <p className="truncate text-xs text-muted-foreground">Best Win Rate</p>
            </div>
          </CardContent>
        </Card>
      </div>
      <Card>
        <CardContent className="p-4">
          <WinRateBar
            winRate={summary.overallWinRate}
            drawRate={summary.overallDrawRate}
            lossRate={summary.overallLossRate}
            size="md"
          />
          <div className="mt-2 flex justify-between text-xs tabular-nums text-muted-foreground">
            <span className="flex items-center gap-1.5">
              <span className="inline-block h-2 w-2 rounded-full bg-emerald-500" />
              Win {summary.overallWinRate}%
            </span>
            <span className="flex items-center gap-1.5">
              <span className="inline-block h-2 w-2 rounded-full bg-amber-400" />
              Draw {summary.overallDrawRate}%
            </span>
            <span className="flex items-center gap-1.5">
              <span className="inline-block h-2 w-2 rounded-full bg-rose-500" />
              Loss {summary.overallLossRate}%
            </span>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
