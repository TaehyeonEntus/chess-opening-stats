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
import { Grid2X2, Search, SlidersHorizontal, Undo2 } from "lucide-react"

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
  epdMap: Map<string, { id: number; eco: string; name: string; epd: string }>
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
  epdMap,
  search,
  onSearchChange,
}: OpeningFilterProps) {
  const t = useTranslations("opening")
  const tCommon = useTranslations("common")
  
  const gameRef = useRef(new Chess())
  const [isBoardOpen, setIsBoardOpen] = useState(false)
  const [boardOrientation, setBoardOrientation] = useState<"white" | "black">("white")
  const [fen, setFen] = useState(gameRef.current.fen())
  const [boardEpd, setBoardEpd] = useState("")

  const boardStatW = useMemo(() => {
    if (!boardEpd) return null
    return allOpenings.find((op) => op.epd === boardEpd && op.color === "white") || null
  }, [allOpenings, boardEpd])

  const boardStatB = useMemo(() => {
    if (!boardEpd) return null
    return allOpenings.find((op) => op.epd === boardEpd && op.color === "black") || null
  }, [allOpenings, boardEpd])

  const fallbackOpening = useMemo(() => {
    if (boardStatW || boardStatB || !boardEpd) return null
    return epdMap.get(boardEpd) || null
  }, [boardStatW, boardStatB, boardEpd, epdMap])

  const currentBoardOpeningName = (boardStatW || boardStatB)?.name || fallbackOpening?.name
  const currentBoardOpeningEco = (boardStatW || boardStatB)?.eco || fallbackOpening?.eco

  function handlePieceDrop(args: { sourceSquare: string; targetSquare: string | null }) {
    if (!args.targetSquare) return false
    
    try {
      const move = gameRef.current.move({
        from: args.sourceSquare,
        to: args.targetSquare,
        promotion: "q",
      })
      
      if (!move) return false
      
      const nextFen = gameRef.current.fen()
      setFen(nextFen)
      setBoardEpd(fenToEpd(nextFen))
      return true
    } catch (e) {
      return false
    }
  }

  function handleUndo() {
    const move = gameRef.current.undo()
    if (move) {
      const nextFen = gameRef.current.fen()
      setFen(nextFen)
      setBoardEpd(fenToEpd(nextFen))
    }
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
          <DialogContent className="max-w-6xl max-h-[95vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>{t("boardSearchTitle")}</DialogTitle>
              <DialogDescription>{t("boardSearchDescription")}</DialogDescription>
            </DialogHeader>

            <div className="grid grid-cols-1 lg:grid-cols-[400px_1fr] gap-6 p-1 h-full min-h-0">
              {/* Left Column: Board and Controls */}
              <div className="flex flex-col gap-4 h-full">
                <div className="aspect-square rounded-lg border bg-card shrink-0">
                  <Chessboard
                    key={fen + boardOrientation}
                    options={{
                      position: fen,
                      onPieceDrop: handlePieceDrop,
                      boardOrientation: boardOrientation,
                      animationDurationInMs: 200,
                      showNotation: true,
                      darkSquareStyle: { backgroundColor: "#769656" },
                      lightSquareStyle: { backgroundColor: "#e9edcc" },
                    }}
                  />
                </div>
                <div className="flex gap-2 shrink-0">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleUndo}
                    className="flex-1 gap-1.5"
                  >
                    <Undo2 className="h-3.5 w-3.5" />
                    {tCommon("undo")}
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleFlipBoard}
                    className="flex-1"
                  >
                    {t("flipBoard")}
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleClearBoardSearch}
                    className="flex-none px-3"
                    title={t("resetBoard")}
                  >
                    {tCommon("reset")}
                  </Button>
                </div>
              </div>

              {/* Right Column: Stacked Info and Stats (2:4:4 Ratio) */}
              <div className="flex flex-col gap-4 h-full min-h-0">
                {/* Row 1: Opening Info (Flex 2) */}
                <div className="flex-[2] rounded-lg border bg-muted/30 p-4 flex flex-col justify-center min-h-0 min-w-0 overflow-hidden">
                  <div className="text-[10px] uppercase font-bold text-muted-foreground mb-1 shrink-0">{t("opening")}</div>
                  {currentBoardOpeningName ? (
                      <div className="flex flex-col gap-1 min-w-0">
                        <div className="flex items-center justify-between gap-2">
                          <div className="text-sm font-mono text-primary font-semibold shrink-0">
                            {currentBoardOpeningEco}
                          </div>
                        </div>
                        <h3 className="text-xl font-bold leading-tight break-words">
                          {currentBoardOpeningName}
                        </h3>
                      </div>
                  ) : (
                    <div className="text-sm text-muted-foreground italic shrink-0">{t("unknownOrNoGames")}</div>
                  )}
                </div>

                {/* Row 2: White Stats (Flex 4) */}
                <div className="flex-[4] rounded-lg border bg-card p-4 shadow-sm flex flex-col justify-center min-h-0 min-w-0 overflow-hidden">
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="inline-block h-3.5 w-3.5 rounded-full border border-gray-300 bg-white" />
                      <h4 className="text-sm font-semibold">{t("playedAsWhite")}</h4>
                    </div>
                    {boardStatW ? (
                      <div className="text-xs font-medium text-muted-foreground">
                        {boardStatW.totalGames} {t("games")}
                      </div>
                    ) : null}
                  </div>
                  {boardStatW ? (
                    <div className="flex items-center gap-8">
                      <div className="flex flex-col">
                        <div className="text-[10px] text-muted-foreground uppercase font-semibold">{t("winRate")}</div>
                        <div className="text-2xl font-black">{boardStatW.winRate}%</div>
                      </div>
                      <div className="flex-1 space-y-2">
                        <div className="flex gap-1 h-2.5">
                          <div
                            className="rounded-l-sm bg-emerald-500 shadow-sm"
                            style={{ width: `${boardStatW.winRate}%` }}
                          />
                          <div 
                            className="bg-amber-400 shadow-sm" 
                            style={{ width: `${boardStatW.drawRate}%` }} 
                          />
                          <div
                            className="rounded-r-sm bg-rose-500 shadow-sm"
                            style={{ width: `${boardStatW.lossRate}%` }}
                          />
                        </div>
                        <div className="flex justify-between text-[11px] tabular-nums font-medium text-muted-foreground">
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-emerald-500" /> {boardStatW.wins}W</span>
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-amber-400" /> {boardStatW.draws}D</span>
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-rose-500" /> {boardStatW.losses}L</span>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="text-center">
                      <p className="text-xs text-muted-foreground italic">
                        {t("noDataAvailable") || "No data"}
                      </p>
                    </div>
                  )}
                </div>

                {/* Row 3: Black Stats (Flex 4) */}
                <div className="flex-[4] rounded-lg border bg-card p-4 shadow-sm flex flex-col justify-center min-h-0 min-w-0 overflow-hidden">
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="inline-block h-3.5 w-3.5 rounded-full border border-gray-700 bg-black" />
                      <h4 className="text-sm font-semibold">{t("playedAsBlack")}</h4>
                    </div>
                    {boardStatB ? (
                      <div className="text-xs font-medium text-muted-foreground">
                        {boardStatB.totalGames} {t("games")}
                      </div>
                    ) : null}
                  </div>
                  {boardStatB ? (
                    <div className="flex items-center gap-8">
                      <div className="flex flex-col">
                        <div className="text-[10px] text-muted-foreground uppercase font-semibold">{t("winRate")}</div>
                        <div className="text-2xl font-black">{boardStatB.winRate}%</div>
                      </div>
                      <div className="flex-1 space-y-2">
                        <div className="flex gap-1 h-2.5">
                          <div
                            className="rounded-l-sm bg-emerald-500 shadow-sm"
                            style={{ width: `${boardStatB.winRate}%` }}
                          />
                          <div 
                            className="bg-amber-400 shadow-sm" 
                            style={{ width: `${boardStatB.drawRate}%` }} 
                          />
                          <div
                            className="rounded-r-sm bg-rose-500 shadow-sm"
                            style={{ width: `${boardStatB.lossRate}%` }}
                          />
                        </div>
                        <div className="flex justify-between text-[11px] tabular-nums font-medium text-muted-foreground">
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-emerald-500" /> {boardStatB.wins}W</span>
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-amber-400" /> {boardStatB.draws}D</span>
                          <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-rose-500" /> {boardStatB.losses}L</span>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div className="text-center">
                      <p className="text-xs text-muted-foreground italic">
                        {t("noDataAvailable") || "No data"}
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
