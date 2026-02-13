"use client"

import { useState, useMemo, useCallback } from "react"
import { Chessboard } from "react-chessboard"
import { Chess } from "chess.js"
import type { Square } from "chess.js"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { WinRateBar } from "@/components/win-rate-bar"
import type { Opening, OpeningResult, OpeningStatView } from "@/lib/types"
import { calcRates } from "@/lib/calc"
import { RotateCcw, ChevronLeft, HelpCircle } from "lucide-react"

interface BoardExplorerProps {
  openings: Opening[]
  results: OpeningResult[]
}

function fenToEpd(fen: string): string {
  // EPD is FEN without halfmove clock and fullmove counter
  const parts = fen.split(" ")
  return parts.slice(0, 4).join(" ")
}

function findMatchingOpening(
  epd: string,
  openings: Opening[]
): Opening | undefined {
  return openings.find((o) => o.epd === epd)
}

function getStatsForOpening(
  opening: Opening,
  results: OpeningResult[]
): OpeningStatView | null {
  const matching = results.filter((r) => r.epd === opening.epd)
  if (matching.length === 0) return null

  const totals = matching.reduce(
    (acc, r) => ({
      wins: acc.wins + r.wins,
      draws: acc.draws + r.draws,
      losses: acc.losses + r.losses,
    }),
    { wins: 0, draws: 0, losses: 0 }
  )

  const rates = calcRates(totals.wins, totals.draws, totals.losses)
  return {
    eco: opening.eco,
    name: opening.name,
    epd: opening.epd,
    color: "all",
    wins: totals.wins,
    draws: totals.draws,
    losses: totals.losses,
    ...rates,
  }
}

export function BoardExplorer({ openings, results }: BoardExplorerProps) {
  const [game, setGame] = useState(new Chess())
  const [moveHistory, setMoveHistory] = useState<string[]>([])

  const currentEpd = useMemo(() => fenToEpd(game.fen()), [game])

  const matchedOpening = useMemo(
    () => findMatchingOpening(currentEpd, openings),
    [currentEpd, openings]
  )

  const matchedStats = useMemo(() => {
    if (!matchedOpening) return null
    return getStatsForOpening(matchedOpening.eco, results)
  }, [matchedOpening, results])

  // Find all openings reachable from current position (next moves)
  const nextMoveOpenings = useMemo(() => {
    const legalMoves = game.moves({ verbose: true })
    const reachable: { move: string; opening: Opening; stats: OpeningStatView | null }[] = []
    const seen = new Set<string>()

    for (const move of legalMoves) {
      const testGame = new Chess(game.fen())
      testGame.move(move)
      const testEpd = fenToEpd(testGame.fen())
      const opening = findMatchingOpening(testEpd, openings)
      if (opening && !seen.has(opening.eco)) {
        seen.add(opening.eco)
        reachable.push({
          move: move.san,
          opening,
          stats: getStatsForOpening(opening.eco, results),
        })
      }
    }

    return reachable.sort(
      (a, b) => (b.stats?.totalGames ?? 0) - (a.stats?.totalGames ?? 0)
    )
  }, [game, openings, results])

  const onDrop = useCallback(
    (sourceSquare: Square, targetSquare: Square) => {
      const gameCopy = new Chess(game.fen())
      const move = gameCopy.move({
        from: sourceSquare,
        to: targetSquare,
        promotion: "q",
      })
      if (move === null) return false
      setGame(gameCopy)
      setMoveHistory((prev) => [...prev, move.san])
      return true
    },
    [game]
  )

  const goBack = useCallback(() => {
    const gameCopy = new Chess(game.fen())
    gameCopy.undo()
    setGame(gameCopy)
    setMoveHistory((prev) => prev.slice(0, -1))
  }, [game])

  const reset = useCallback(() => {
    setGame(new Chess())
    setMoveHistory([])
  }, [])

  return (
    <div className="flex flex-col gap-4 lg:flex-row lg:gap-6">
      {/* Board */}
      <div className="flex flex-col gap-3">
        <div className="mx-auto w-full max-w-[400px]">
          <Chessboard
            id="board-explorer"
            position={game.fen()}
            onPieceDrop={onDrop}
            boardWidth={400}
            customDarkSquareStyle={{ backgroundColor: "hsl(240 5% 22%)" }}
            customLightSquareStyle={{ backgroundColor: "hsl(40 10% 82%)" }}
            customBoardStyle={{
              borderRadius: "6px",
              overflow: "hidden",
            }}
          />
        </div>
        <div className="mx-auto flex w-full max-w-[400px] items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={goBack}
            disabled={moveHistory.length === 0}
            className="gap-1.5"
          >
            <ChevronLeft className="h-3.5 w-3.5" />
            Undo
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={reset}
            disabled={moveHistory.length === 0}
            className="gap-1.5"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            Reset
          </Button>
          {moveHistory.length > 0 && (
            <div className="flex flex-1 items-center gap-1 overflow-x-auto px-2">
              {moveHistory.map((move, i) => (
                <span
                  key={i}
                  className="shrink-0 text-xs tabular-nums text-muted-foreground"
                >
                  {i % 2 === 0 && (
                    <span className="text-foreground/50">
                      {Math.floor(i / 2) + 1}.
                    </span>
                  )}
                  {move}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Info Panel */}
      <div className="flex min-w-0 flex-1 flex-col gap-3">
        {/* Current position info */}
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Current Position
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            {matchedOpening ? (
              <div className="flex flex-col gap-3">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-lg font-bold text-foreground">
                      {matchedOpening.name}
                    </p>
                    <Badge variant="outline" className="mt-1 font-mono text-xs">
                      {matchedOpening.eco}
                    </Badge>
                  </div>
                  {matchedStats && (
                    <div className="text-right">
                      <span className="text-2xl font-bold tabular-nums text-emerald-500">
                        {matchedStats.winRate}%
                      </span>
                      <p className="text-xs text-muted-foreground">win rate</p>
                    </div>
                  )}
                </div>
                {matchedStats && (
                  <>
                    <WinRateBar
                      winRate={matchedStats.winRate}
                      drawRate={matchedStats.drawRate}
                      lossRate={matchedStats.lossRate}
                      size="md"
                    />
                    <div className="flex justify-between text-xs tabular-nums text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <span className="inline-block h-2 w-2 rounded-full bg-emerald-500" />
                        {matchedStats.wins}W
                      </span>
                      <span className="flex items-center gap-1">
                        <span className="inline-block h-2 w-2 rounded-full bg-amber-400" />
                        {matchedStats.draws}D
                      </span>
                      <span className="flex items-center gap-1">
                        <span className="inline-block h-2 w-2 rounded-full bg-rose-500" />
                        {matchedStats.losses}L
                      </span>
                      <span className="text-foreground/60">
                        {matchedStats.totalGames.toLocaleString()} games
                      </span>
                    </div>
                  </>
                )}
              </div>
            ) : (
              <div className="flex items-center gap-2 py-2 text-sm text-muted-foreground">
                <HelpCircle className="h-4 w-4 shrink-0" />
                {moveHistory.length === 0
                  ? "Make a move to explore openings"
                  : "No matching opening in database"}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Next moves suggestions */}
        {nextMoveOpenings.length > 0 && (
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Next Moves ({nextMoveOpenings.length} openings)
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-0">
              <div className="flex flex-col gap-2">
                {nextMoveOpenings.map(({ move, opening, stats }) => (
                  <button
                    key={opening.eco}
                    onClick={() => {
                      const gameCopy = new Chess(game.fen())
                      gameCopy.move(move)
                      setGame(gameCopy)
                      setMoveHistory((prev) => [...prev, move])
                    }}
                    className="group flex items-center justify-between gap-2 rounded-md border border-border bg-card px-3 py-2 text-left transition-colors hover:border-foreground/20 hover:bg-accent"
                  >
                    <div className="flex items-center gap-2.5 overflow-hidden">
                      <span className="shrink-0 font-mono text-sm font-semibold text-foreground">
                        {move}
                      </span>
                      <span className="truncate text-sm text-muted-foreground">
                        {opening.name}
                      </span>
                      <Badge
                        variant="outline"
                        className="shrink-0 font-mono text-xs"
                      >
                        {opening.eco}
                      </Badge>
                    </div>
                    {stats && (
                      <div className="flex shrink-0 items-center gap-3">
                        <span className="text-xs tabular-nums text-muted-foreground">
                          {stats.totalGames.toLocaleString()}
                        </span>
                        <span className="text-sm font-semibold tabular-nums text-emerald-500">
                          {stats.winRate}%
                        </span>
                      </div>
                    )}
                  </button>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  )
}
