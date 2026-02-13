import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { WinRateBar } from "@/components/win-rate-bar"
import type { OpeningStatView } from "@/lib/types"

interface OpeningCardProps {
  stat: OpeningStatView
}

export function OpeningCard({ stat }: OpeningCardProps) {
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
              {stat.color !== "all" && (
                <span
                  className={`inline-flex h-4 w-4 items-center justify-center rounded-full border ${
                    stat.color === "white"
                      ? "border-foreground/20 bg-background"
                      : "border-foreground/20 bg-foreground"
                  }`}
                  aria-label={stat.color === "white" ? "White pieces" : "Black pieces"}
                />
              )}
            </div>
          </div>
          <div className="text-right">
            <span className="text-2xl font-bold tabular-nums text-emerald-500">
              {stat.winRate}%
            </span>
            <p className="text-xs text-muted-foreground">win rate</p>
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
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
          <span className="text-foreground/60">
            {stat.totalGames} games
          </span>
        </div>
      </CardContent>
    </Card>
  )
}
