"use client"

import { useMemo, useRef, useState } from "react"
import { useTranslations } from "next-intl"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import type { ColorFilter, OpeningStatView, SortBy } from "@/lib/types"
import { Chess } from "chess.js"
import { Chessboard } from "react-chessboard"
import { Grid2X2, Search, SlidersHorizontal } from "lucide-react"

interface OpeningFilterProps {
  colorFilter: ColorFilter
  onColorFilterChange: (filter: ColorFilter) => void
  sortBy: SortBy
  onSortByChange: (sortBy: SortBy) => void
  minGames: string
  onMinGamesChange: (minGames: string) => void
  maxGames: string
  onMaxGamesChange: (maxGames: string) => void
  allOpenings: OpeningStatView[]
  search: string
  onSearchChange: (search: string) => void
}

const BOARD_STORAGE_KEY = "boardSearchFen"
const DEFAULT_FEN = new Chess().fen()

function fenToEpd(fen: string) {
  return fen.split(" ").slice(0, 4).join(" ")
}

export function OpeningFilter({
  colorFilter,
  onColorFilterChange,
  sortBy,
  onSortByChange,
  minGames,
  onMinGamesChange,
  maxGames,
  onMaxGamesChange,
  allOpenings,
  search,
  onSearchChange,
}: OpeningFilterProps) {
  const t = useTranslations("opening")
  const tCommon = useTranslations("common")
  const gameRef = useRef(new Chess())
  const [isBoardOpen, setIsBoardOpen] = useState(false)
  const [fen, setFen] = useState(DEFAULT_FEN)
  const [boardEpd, setBoardEpd] = useState("")
  const [boardOrientation, setBoardOrientation] = useState<"white" | "black">("white")

  const whiteStats = useMemo(() => {
    if (!boardEpd.trim()) return null
    return allOpenings.find((op) => op.epd === boardEpd && op.color === "white") || null
  }, [allOpenings, boardEpd])

  const blackStats = useMemo(() => {
    if (!boardEpd.trim()) return null
    return allOpenings.find((op) => op.epd === boardEpd && op.color === "black") || null
  }, [allOpenings, boardEpd])

  function updateFen(nextFen: string) {
    gameRef.current.load(nextFen)
    const resolvedFen = gameRef.current.fen()
    setFen(resolvedFen)
    setBoardEpd(fenToEpd(resolvedFen))
  }

  function handlePieceDrop(args: { sourceSquare: string; targetSquare: string | null }) {
    if (!args.targetSquare) {
      return false
    }
    const move = gameRef.current.move({
      from: args.sourceSquare,
      to: args.targetSquare,
      promotion: "q",
    })
    if (!move) {
      return false
    }
    updateFen(gameRef.current.fen())
    return true
  }

  function handleClearBoardSearch() {
    gameRef.current.reset()
    const resetFen = gameRef.current.fen()
    setFen(resetFen)
    setBoardEpd("")
  }

  function handleFlipBoard() {
    setBoardOrientation((prev) => (prev === "white" ? "black" : "white"))
  }

  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <Tabs
        value={colorFilter}
        onValueChange={(v) => onColorFilterChange(v as ColorFilter)}
      >
        <TabsList>
          <TabsTrigger value="all">{t("all")}</TabsTrigger>
          <TabsTrigger value="white">{t("white")}</TabsTrigger>
          <TabsTrigger value="black">{t("black")}</TabsTrigger>
        </TabsList>
      </Tabs>

      <div className="flex items-center gap-2">
        <Dialog open={isBoardOpen} onOpenChange={setIsBoardOpen}>
          <DialogTrigger asChild>
            <Button type="button" variant="outline" className="h-10 w-10 p-0" aria-label={t("boardSearchAriaLabel")}>
              <Grid2X2 className="h-4 w-4" />
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-6xl max-h-[90vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>{t("boardSearchTitle")}</DialogTitle>
              <DialogDescription>{t("boardSearchDescription")}</DialogDescription>
            </DialogHeader>

            <div className="grid gap-6 lg:grid-cols-[400px_1fr]">
              <div className="space-y-3">
                <div className="rounded-lg border bg-card p-4">
                  <Chessboard
                    options={{
                      position: fen,
                      allowDragging: true,
                      showNotation: false,
                      onPieceDrop: handlePieceDrop,
                      boardOrientation: boardOrientation,
                    }}
                  />
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleClearBoardSearch}
                    className="flex-1"
                  >
                    {t("resetBoard")}
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleFlipBoard}
                    className="flex-1"
                  >
                    {t("flipBoard")}
                  </Button>
                </div>
              </div>

              <div className="flex flex-col gap-4 min-h-0">
                {(whiteStats || blackStats) && (
                  <div className="rounded-lg border bg-card p-3">
                    <div className="text-xs text-muted-foreground mb-1">{t("opening")}</div>
                    <div className="font-semibold">{(whiteStats || blackStats)!.name}</div>
                    <div className="text-sm font-mono text-muted-foreground mt-0.5">{(whiteStats || blackStats)!.eco}</div>
                  </div>
                )}
                
                {/* White Stats */}
                <div className="flex-1 rounded-lg border bg-card p-5 shadow-sm flex flex-col min-h-0">
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="inline-block h-4 w-4 rounded-full border border-gray-300 bg-white" />
                      <h4 className="text-base font-semibold">{t("playedAsWhite")}</h4>
                    </div>
                    {whiteStats && <div className="text-sm font-medium text-muted-foreground">{whiteStats.totalGames} {t("games")}</div>}
                  </div>
                  {whiteStats ? (
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <div className="text-sm text-muted-foreground">{t("winRate")}</div>
                        <div className="text-2xl font-bold">{whiteStats.winRate}%</div>
                      </div>
                      <div className="space-y-2">
                        <div className="flex gap-1">
                          <div
                            className="h-2 rounded-l bg-emerald-500"
                            style={{ width: `${whiteStats.winRate}%` }}
                          />
                          <div className="h-2 bg-amber-400" style={{ width: `${whiteStats.drawRate}%` }} />
                          <div
                            className="h-2 rounded-r bg-rose-500"
                            style={{ width: `${whiteStats.lossRate}%` }}
                          />
                        </div>
                        <div className="flex justify-between text-sm tabular-nums text-muted-foreground">
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-emerald-500" />
                            {whiteStats.wins}W
                          </span>
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-amber-400" />
                            {whiteStats.draws}D
                          </span>
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-rose-500" />
                            {whiteStats.losses}L
                          </span>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="flex h-32 items-center justify-center">
                      <p className="text-sm text-center text-muted-foreground">
                        {t("unknownOrNoGames")}
                      </p>
                    </div>
                  )}
                </div>

                {/* Black Stats */}
                <div className="flex-1 rounded-lg border bg-card p-5 shadow-sm flex flex-col min-h-0">
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="inline-block h-4 w-4 rounded-full border border-gray-700 bg-black" />
                      <h4 className="text-base font-semibold">{t("playedAsBlack")}</h4>
                    </div>
                    {blackStats && <div className="text-sm font-medium text-muted-foreground">{blackStats.totalGames} {t("games")}</div>}
                  </div>
                  {blackStats ? (
                    <div className="space-y-3">
                      <div className="flex items-center justify-between">
                        <div className="text-sm text-muted-foreground">{t("winRate")}</div>
                        <div className="text-2xl font-bold">{blackStats.winRate}%</div>
                      </div>
                      <div className="space-y-2">
                        <div className="flex gap-1">
                          <div
                            className="h-2 rounded-l bg-emerald-500"
                            style={{ width: `${blackStats.winRate}%` }}
                          />
                          <div className="h-2 bg-amber-400" style={{ width: `${blackStats.drawRate}%` }} />
                          <div
                            className="h-2 rounded-r bg-rose-500"
                            style={{ width: `${blackStats.lossRate}%` }}
                          />
                        </div>
                        <div className="flex justify-between text-sm tabular-nums text-muted-foreground">
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-emerald-500" />
                            {blackStats.wins}W
                          </span>
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-amber-400" />
                            {blackStats.draws}D
                          </span>
                          <span className="flex items-center gap-1.5">
                            <span className="inline-block h-2.5 w-2.5 rounded-full bg-rose-500" />
                            {blackStats.losses}L
                          </span>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="flex h-32 items-center justify-center">
                      <p className="text-sm text-center text-muted-foreground">
                        {t("unknownOrNoGames")}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </DialogContent>
        </Dialog>

        <div className="relative flex-1 sm:w-56 sm:flex-none">
          <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder={t("searchPlaceholder")}
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-9"
          />
        </div>

        <Popover>
          <PopoverTrigger asChild>
            <Button variant="outline" className="w-28 gap-1.5">
              <SlidersHorizontal className="h-4 w-4 text-muted-foreground" />
              {tCommon("filter")}
            </Button>
          </PopoverTrigger>
          <PopoverContent align="end" className="w-72 space-y-4">
            <div className="space-y-2">
              <p className="text-sm font-medium">{t("sortBy")}</p>
              <Select value={sortBy} onValueChange={(v) => onSortByChange(v as SortBy)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="winRate">{t("sortByWinRate")}</SelectItem>
                  <SelectItem value="totalGames">{t("sortByTotalGames")}</SelectItem>
                  <SelectItem value="name">{t("sortByName")}</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <p className="text-sm font-medium">{t("gamesRange")}</p>
              <div className="grid grid-cols-2 gap-2">
                <Input
                  type="number"
                  inputMode="numeric"
                  min={0}
                  placeholder={t("min")}
                  value={minGames}
                  onChange={(e) => onMinGamesChange(e.target.value)}
                />
                <Input
                  type="number"
                  inputMode="numeric"
                  min={0}
                  placeholder={t("max")}
                  value={maxGames}
                  onChange={(e) => onMaxGamesChange(e.target.value)}
                />
              </div>
            </div>
          </PopoverContent>
        </Popover>
      </div>
    </div>
  )
}
