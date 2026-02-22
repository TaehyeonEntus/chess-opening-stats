"use client"

import { useState, useEffect, useMemo, useDeferredValue, useCallback } from "react"
import { useTranslations, useLocale } from "next-intl"
import { Link, useRouter } from "@/i18n/navigation"
import { OpeningGrid } from "@/components/opening-grid"
import { OpeningSummary } from "@/components/opening-summary"
import { OpeningFilter } from "@/components/opening-filter"
import { Skeleton } from "@/components/ui/skeleton"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { provideAddPlayer, provideAllOpeningResult, provideSummary, provideSyncAccount } from "@/lib/provide/provideFacade"
import { logout, changePassword, deleteAccount } from "@/lib/api/auth"
import { fetchAccountInfo, deletePlayer } from "@/lib/api/api"
import { calculateRatesFromCounts } from "@/lib/stats"
import { AxiosError } from "axios"
import { toast } from "sonner"
import { ThemeToggle } from "@/components/theme-toggle"
import { LanguageToggle } from "@/components/language-toggle"
import { ConfirmDialog } from "@/components/confirm-dialog"
import { User, RefreshCw, KeyRound, LogOut, Trash2, Eye, EyeOff, Loader2 } from "lucide-react"
import type { OpeningStatView, ColorFilter, DisplaySummary, Stat, WinRate, SortBy, Platform, AccountInfoResponse } from "@/lib/types"

function calculateDisplaySummary(
  winRates: WinRate[],
  bestWinRateOpenings: Stat[],
  mostPlayedOpenings: Stat[],
  filter: ColorFilter
): DisplaySummary {
  const filteredWinRates = filter === "all" ? winRates : winRates.filter((rate) => rate.color === filter);
  const totalWins = filteredWinRates.reduce((sum, rate) => sum + rate.wins, 0);
  const totalDraws = filteredWinRates.reduce((sum, rate) => sum + rate.draws, 0);
  const totalLosses = filteredWinRates.reduce((sum, rate) => sum + rate.losses, 0);
  const rates = calculateRatesFromCounts(totalWins, totalDraws, totalLosses)

  const filteredBestWinRateOpenings = filter === "all" ? bestWinRateOpenings : bestWinRateOpenings.filter((op) => op.color === filter);
  const filteredMostPlayedOpenings = filter === "all" ? mostPlayedOpenings : mostPlayedOpenings.filter((op) => op.color === filter);

  return {
    totalWins,
    totalDraws,
    totalLosses,
    totalGames: rates.totalGames,
    winRate: rates.winRate,
    drawRate: rates.drawRate,
    lossRate: rates.lossRate,
    bestWinRateOpenings: filteredBestWinRateOpenings,
    mostPlayedOpenings: filteredMostPlayedOpenings,
  };
}

export default function HomePage() {
  const router = useRouter()
  const locale = useLocale()
  const t = useTranslations("home")
  const tAuth = useTranslations("auth")
  const tPlayer = useTranslations("player")
  const tMyPage = useTranslations("myPage")
  const tCommon = useTranslations("common")
  
  const [allOpenings, setAllOpenings] = useState<OpeningStatView[]>([])
  const [summaries, setSummaries] = useState<Record<ColorFilter, DisplaySummary> | null>(null)
  const [nickname, setNickname] = useState<string>("")
  const [colorFilter, setColorFilter] = useState<ColorFilter>("all")
  const [sortBy, setSortBy] = useState<SortBy>("totalGames") // Moved from OpeningGrid
  const [minGames, setMinGames] = useState("")
  const [maxGames, setMaxGames] = useState("")
  const [search, setSearch] = useState("") // Moved from OpeningGrid
  const deferredSearch = useDeferredValue(search)
  const [loading, setLoading] = useState(true)
  const [isAddPlayerOpen, setIsAddPlayerOpen] = useState(false)
  const [playerUsername, setPlayerUsername] = useState("")
  const [playerPlatform, setPlayerPlatform] = useState<Platform>("CHESS_COM")
  const [addingPlayer, setAddingPlayer] = useState(false)
  const [addPlayerError, setAddPlayerError] = useState<string | null>(null)
  const [syncing, setSyncing] = useState(false)
  const [loggingOut, setLoggingOut] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // MyPage states
  const [myPageOpen, setMyPageOpen] = useState(false)
  const [accountInfo, setAccountInfo] = useState<AccountInfoResponse | null>(null)
  const [loadingAccountInfo, setLoadingAccountInfo] = useState(false)
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false)
  const [deleteAccountDialogOpen, setDeleteAccountDialogOpen] = useState(false)
  const [removePlayerDialog, setRemovePlayerDialog] = useState<{open: boolean, username: string, platform: Platform | null}>({
    open: false,
    username: "",
    platform: null
  })
  const [changePasswordDialogOpen, setChangePasswordDialogOpen] = useState(false)
  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [newPasswordConfirm, setNewPasswordConfirm] = useState("")
  const [showOldPassword, setShowOldPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)
  const [showNewPasswordConfirm, setShowNewPasswordConfirm] = useState(false)
  const [changingPassword, setChangingPassword] = useState(false)
  const [passwordError, setPasswordError] = useState<string | null>(null)
  const [deletingAccount, setDeletingAccount] = useState(false)
  const [removingPlayer, setRemovingPlayer] = useState(false)

  const loadData = useCallback(async (showLoading = true) => {
    if (showLoading) {
      setLoading(true)
    }
    setError(null)

    const [openingsData, summaryData] = await Promise.all([
      provideAllOpeningResult(),
      provideSummary(),
    ])

    setAllOpenings(openingsData)
    const { nickname, winRates, bestWinRateOpenings, mostPlayedOpenings } = summaryData
    setNickname(nickname)
    setSummaries({
      all: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "all"),
      white: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "white"),
      black: calculateDisplaySummary(winRates, bestWinRateOpenings, mostPlayedOpenings, "black"),
    })

    if (showLoading) {
      setLoading(false)
    }
  }, [])

  // MyPage functions
  const loadAccountInfo = async () => {
    try {
      setLoadingAccountInfo(true)
      const data = await fetchAccountInfo()
      setAccountInfo(data)
    } catch (err) {
      console.error("Failed to load account info", err)
      toast.error(tMyPage("failedToLoadAccountInfo"))
    } finally {
      setLoadingAccountInfo(false)
    }
  }

  const formatDateTime = (isoString: string) => {
    const date = new Date(isoString)
    return date.toLocaleString(locale === 'ko' ? "ko-KR" : "en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  const handleChangePassword = () => {
    setChangePasswordDialogOpen(true)
    setOldPassword("")
    setNewPassword("")
    setNewPasswordConfirm("")
    setShowOldPassword(false)
    setShowNewPassword(false)
    setShowNewPasswordConfirm(false)
    setPasswordError(null)
  }

  const handleChangePasswordSubmit = async () => {
    if (!oldPassword || !newPassword || !newPasswordConfirm) {
      setPasswordError(tAuth("allFieldsRequired"))
      return
    }

    if (newPassword !== newPasswordConfirm) {
      setPasswordError(tAuth("newPasswordMismatch"))
      return
    }

    try {
      setChangingPassword(true)
      setPasswordError(null)
      await changePassword({ oldPassword, newPassword, newPasswordConfirm })
      toast.success(tAuth("changePasswordSuccess"))
      setChangePasswordDialogOpen(false)
      setOldPassword("")
      setNewPassword("")
      setNewPasswordConfirm("")
    } catch (err) {
      if (err instanceof AxiosError) {
        const errorCode = err.response?.data?.code
        if (errorCode === "S003") {
          setPasswordError(tAuth("invalidPassword"))
        } else if (errorCode === "S004") {
          setPasswordError(tAuth("newPasswordMismatch"))
        } else {
          setPasswordError(err.response?.data?.message || tAuth("changePasswordFailed"))
        }
      } else {
        setPasswordError(tAuth("changePasswordFailed"))
      }
      console.error(err)
    } finally {
      setChangingPassword(false)
    }
  }

  const handleDeleteAccount = async () => {
    if (deletingAccount) {
      return
    }

    try {
      setDeletingAccount(true)
      await deleteAccount()
      toast.success(tMyPage("deleteAccountSuccess"))
      router.replace("/login")
    } catch (err) {
      if (err instanceof AxiosError) {
        const message = err.response?.data?.message || tMyPage("deleteAccountFailed")
        toast.error(message)
      } else {
        toast.error(tMyPage("deleteAccountFailed"))
      }
      console.error(err)
    } finally {
      setDeletingAccount(false)
    }
  }

  const handleRemovePlayer = async (username: string, platform: Platform | null) => {
    if (!platform || removingPlayer) {
      return
    }

    try {
      setRemovingPlayer(true)
      await deletePlayer({ username, platform })
      toast.success(tPlayer("removeSuccess"))
      await loadAccountInfo()
      // 메인 페이지 데이터도 갱신
      await loadData(false)
    } catch (err) {
      if (err instanceof AxiosError) {
        const message = err.response?.data?.message || tPlayer("removeFailed")
        toast.error(message)
      } else {
        toast.error(tPlayer("removeFailed"))
      }
      console.error(err)
    } finally {
      setRemovingPlayer(false)
    }
  }

  useEffect(() => {
    let cancelled = false

    async function load() {
      try {
        await loadData(true)
      } catch (err) {
        if (!cancelled) {
          console.error("Failed to load data", err)
          setError("Failed to load opening stats. Please try again.")
          setLoading(false)
        }
      }
    }

    load()

    return () => {
      cancelled = true
    }
  }, [loadData])

  useEffect(() => {
    if (myPageOpen && !accountInfo) {
      loadAccountInfo()
    }
  }, [myPageOpen])

  // Filtering and sorting logic moved to HomePage
  const filteredAndSortedOpenings = useMemo(() => {
    let list = [...allOpenings]

    // 색상 필터
    if (colorFilter !== "all") {
      list = list.filter((s) => s.color === colorFilter)
    }

    // 검색 필터
    if (deferredSearch.trim()) {
      const q = deferredSearch.toLowerCase()
      list = list.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.eco.toLowerCase().includes(q)
      )
    }

    const parsedMinGames = Number(minGames)
    const parsedMaxGames = Number(maxGames)
    const hasMinGames = minGames.trim() !== "" && !Number.isNaN(parsedMinGames)
    const hasMaxGames = maxGames.trim() !== "" && !Number.isNaN(parsedMaxGames)

    if (hasMinGames) {
      list = list.filter((s) => s.totalGames >= parsedMinGames)
    }

    if (hasMaxGames) {
      list = list.filter((s) => s.totalGames <= parsedMaxGames)
    }

    // 정렬
    list.sort((a, b) => {
      switch (sortBy) {
        case "winRate":
          return b.winRate - a.winRate
        case "totalGames":
          return b.totalGames - a.totalGames
        case "name":
          return a.name.localeCompare(b.name)
        default:
          return 0
      }
    })

    return list
  }, [allOpenings, colorFilter, deferredSearch, maxGames, minGames, sortBy])

  const currentSummary = summaries ? summaries[colorFilter] : null;
  const summaryWhite = summaries ? summaries.white : null
  const summaryBlack = summaries ? summaries.black : null

  async function handleLogout() {
    try {
      setLoggingOut(true)
      await logout()
      toast.success(tAuth("logoutSuccess"))
      router.replace("/login")
      router.refresh()
    } catch (err) {
      console.error("Failed to logout", err)
      toast.error(tAuth("logoutFailed"))
    } finally {
      setLoggingOut(false)
    }
  }

  async function handleSync() {
    try {
      setSyncing(true)
      setError(null)
      await provideSyncAccount()
    } catch (err) {
      console.error("Failed to sync account players", err)
      if (err instanceof AxiosError) {
        const status = err.response?.status
        const responseData = err.response?.data as { message?: string } | string | undefined
        const responseMessage =
          typeof responseData === "string"
            ? responseData
            : typeof responseData?.message === "string"
            ? responseData.message
            : null

        if (status === 409) {
          toast.warning(responseMessage ?? t("syncInProgress"))
          return
        }
      }
      setError(t("syncFailedTryAgain"))
    } finally {
      setSyncing(false)
    }
  }

  async function handleAddPlayer() {
    if (!playerUsername.trim()) {
      setAddPlayerError(tPlayer("enterUsername"))
      return
    }

    try {
      setAddingPlayer(true)
      setAddPlayerError(null)
      await provideAddPlayer({
        username: playerUsername.trim(),
        platform: playerPlatform,
      })
      setIsAddPlayerOpen(false)
      setPlayerUsername("")
      setPlayerPlatform("CHESS_COM")
      
      // 계정 연동 후 자동 갱신
      toast.success(tPlayer("addSuccess"))
      await handleSync()
      // 마이페이지가 열려 있으면 계정 정보도 갱신
      if (accountInfo) {
        await loadAccountInfo()
      }
    } catch (err) {
      console.error("Failed to add player", err)
      if (err instanceof AxiosError) {
        const status = err.response?.status
        const responseData = err.response?.data as { message?: string } | string | undefined
        const responseCode =
          typeof responseData === "object" && responseData !== null && "code" in responseData
            ? String((responseData as { code?: unknown }).code ?? "")
            : ""
        const responseMessage =
          typeof responseData === "string"
            ? responseData
            : typeof responseData?.message === "string"
            ? responseData.message
            : null

        if (status === 404 || responseCode === "P001") {
          setAddPlayerError(responseMessage ?? tPlayer("addFailed"))
          return
        }

        if (status === 409 || responseCode === "A005") {
          setAddPlayerError(responseMessage ?? "Already linked account.")
          return
        }

        if (responseMessage) {
          setAddPlayerError(responseMessage)
          return
        }
      }
      setAddPlayerError(tPlayer("addFailed"))
    } finally {
      setAddingPlayer(false)
    }
  }

  return (
    <div className="min-h-screen overflow-x-hidden bg-background">
      <div className="absolute top-4 right-4 flex gap-2">
        <ThemeToggle />
        <LanguageToggle />
      </div>
      
      <main className="mx-auto max-w-screen-2xl px-4 py-6 lg:px-6">
        {loading ? (
          <div className="mb-6 rounded-lg border border-border/70 bg-card/50 px-4 py-3 text-sm text-muted-foreground">
            {t("loadingData")}
          </div>
        ) : (
          <header className="mb-6 flex items-center justify-between rounded-lg border border-border/70 bg-card/50 px-4 py-3">
            <p className="text-lg font-semibold">{nickname ? `${nickname}` : tCommon("loading")}</p>
            <Sheet open={myPageOpen} onOpenChange={setMyPageOpen}>
              <SheetTrigger asChild>
                <Button variant="outline">
                  {t("myPage")}
                </Button>
              </SheetTrigger>
              <SheetContent className="w-full sm:max-w-xl overflow-y-auto">
                <SheetHeader>
                  <SheetTitle className="flex items-center gap-2">
                    <User className="h-5 w-5" />
                    {t("myPage")}
                  </SheetTitle>
                  <SheetDescription>
                    {tMyPage("accountSettings")}
                  </SheetDescription>
                </SheetHeader>

                {loadingAccountInfo ? (
                  <div className="flex flex-col items-center justify-center py-12 gap-4">
                    <Loader2 className="h-8 w-8 animate-spin text-primary" />
                    <p className="text-sm text-muted-foreground">{tMyPage("loadingAccountInfo")}</p>
                  </div>
                ) : accountInfo ? (
                  <div className="mt-6 space-y-6">
                    {/* Profile Card */}
                    <Card>
                      <CardHeader>
                        <div className="flex items-center gap-3">
                          <User className="h-8 w-8" />
                          <div>
                            <CardTitle className="text-xl">{accountInfo.nickname}</CardTitle>
                            <CardDescription>{tMyPage("profile")}</CardDescription>
                          </div>
                        </div>
                      </CardHeader>
                    </Card>

                    {/* Data Sync Card */}
                    <Card>
                      <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-base">
                          <RefreshCw className="h-4 w-4" />
                          {tMyPage("dataSync")}
                        </CardTitle>
                        <CardDescription className="text-xs">
                          {tMyPage("lastSync")}: {accountInfo.lastSyncedAt ? formatDateTime(accountInfo.lastSyncedAt) : tMyPage("noSyncRecord")}
                        </CardDescription>
                      </CardHeader>
                      <CardContent>
                        <Button onClick={handleSync} disabled={syncing} className="w-full" size="sm">
                          {syncing ? tMyPage("syncing") : tMyPage("syncNow")}
                        </Button>
                      </CardContent>
                    </Card>

                    {/* Linked Players Card */}
                    <Card>
                      <CardHeader>
                        <CardTitle className="text-base">{tMyPage("linkedAccounts")}</CardTitle>
                        <CardDescription className="text-xs">{tMyPage("linkedAccounts")}</CardDescription>
                      </CardHeader>
                      <CardContent className="space-y-3">
                        {accountInfo.players.length === 0 ? (
                          <p className="text-sm text-muted-foreground text-center py-4">
                            {tPlayer("noLinkedAccounts")}
                          </p>
                        ) : (
                          <div className="space-y-3">
                            {accountInfo.players.map((player) => (
                              <div
                                key={player.username}
                                className="flex items-center justify-between rounded-lg border bg-card p-3"
                              >
                                <div className="flex-1 min-w-0">
                                  <div className="flex items-center gap-2">
                                    <p className="font-semibold text-sm truncate">{player.username}</p>
                                    <Badge variant={player.platform === "CHESS_COM" ? "default" : "secondary"} className="text-xs">
                                      {player.platform === "CHESS_COM" ? "Chess.com" : "Lichess"}
                                    </Badge>
                                  </div>
                                  <p className="text-xs text-muted-foreground mt-1">
                                    {tPlayer("lastPlayed")}: {formatDateTime(player.lastPlayedAt)}
                                  </p>
                                </div>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => setRemovePlayerDialog({ open: true, username: player.username, platform: player.platform })}
                                  className="text-destructive text-xs"
                                >
                                  {tPlayer("removePlayer")}
                                </Button>
                              </div>
                            ))}
                          </div>
                        )}
                        <Button
                          variant="outline"
                          className="w-full"
                          onClick={() => setIsAddPlayerOpen(true)}
                          size="sm"
                        >
                          {tPlayer("addPlayer")}
                        </Button>
                      </CardContent>
                    </Card>

                    {/* Account Settings Card */}
                    <Card>
                      <CardHeader>
                        <CardTitle className="text-base">{tMyPage("accountSettings")}</CardTitle>
                        <CardDescription className="text-xs">{tMyPage("accountSettings")}</CardDescription>
                      </CardHeader>
                      <CardContent className="space-y-2">
                        <Button
                          variant="outline"
                          className="w-full justify-start gap-2"
                          onClick={handleChangePassword}
                          size="sm"
                        >
                          <KeyRound className="h-4 w-4" />
                          {tMyPage("changePassword")}
                        </Button>
                        <Separator />
                        <Button
                          variant="outline"
                          className="w-full justify-start gap-2"
                          onClick={() => setLogoutDialogOpen(true)}
                          size="sm"
                        >
                          <LogOut className="h-4 w-4" />
                          {tAuth("logout")}
                        </Button>
                        <Separator />
                        <Button
                          variant="destructive"
                          className="w-full justify-start gap-2"
                          onClick={() => setDeleteAccountDialogOpen(true)}
                          size="sm"
                        >
                          <Trash2 className="h-4 w-4" />
                          {tMyPage("deleteAccount")}
                        </Button>
                      </CardContent>
                    </Card>
                  </div>
                ) : null}
              </SheetContent>
            </Sheet>
          </header>
        )}
        {loading ? (
          <LoadingSkeleton />
        ) : error ? (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
            {error}
          </div>
        ) : allOpenings.length === 0 ? (
          <div className="flex flex-col items-center justify-center gap-4 rounded-lg border border-border/70 bg-card/50 px-8 py-16">
            <div className="text-center">
              <h3 className="text-lg font-semibold mb-2">{t("emptyState.title")}</h3>
              <p className="text-sm text-muted-foreground mb-6">
                {t("emptyState.description")}
              </p>
              <Button onClick={() => setIsAddPlayerOpen(true)} disabled={syncing || addingPlayer} size="lg">
                {t("emptyState.linkAccount")}
              </Button>
            </div>
          </div>
        ) : (
          <div className="flex flex-col gap-6">
            {/* Filter and Search UI at the top */}
            <OpeningFilter
              colorFilter={colorFilter}
              onColorFilterChange={setColorFilter}
              sortBy={sortBy}
              onSortByChange={setSortBy}
              minGames={minGames}
              onMinGamesChange={setMinGames}
              maxGames={maxGames}
              onMaxGamesChange={setMaxGames}
              allOpenings={allOpenings}
              search={search}
              onSearchChange={setSearch}
            />

            {/* Summary Section */}
            {currentSummary && <OpeningSummary summary={currentSummary} colorFilter={colorFilter} />}
            
            {/* Opening Grid */}
            <OpeningGrid stats={filteredAndSortedOpenings} />
          </div>
        )}

        <Dialog
          open={isAddPlayerOpen}
          onOpenChange={(open) => {
            setIsAddPlayerOpen(open)
            if (open) {
              setAddPlayerError(null)
            }
          }}
        >
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{tPlayer("addPlayerTitle")}</DialogTitle>
              <DialogDescription>{tPlayer("addPlayerDescription")}</DialogDescription>
            </DialogHeader>

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="add-player-username">{tPlayer("playerUsername")}</Label>
                <Input
                  id="add-player-username"
                  value={playerUsername}
                  onChange={(e) => setPlayerUsername(e.target.value)}
                  placeholder={tPlayer("usernamePlaceholder")}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="add-player-platform">{tPlayer("platform")}</Label>
                <Select value={playerPlatform} onValueChange={(value) => setPlayerPlatform(value as Platform)}>
                  <SelectTrigger id="add-player-platform">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CHESS_COM">CHESS.COM</SelectItem>
                    <SelectItem value="LICHESS">LICHESS</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {addPlayerError ? (
              <p className="text-sm text-destructive">{addPlayerError}</p>
            ) : null}

            <DialogFooter>
              <Button variant="outline" onClick={() => setIsAddPlayerOpen(false)} disabled={addingPlayer}>
                {tCommon("cancel")}
              </Button>
              <Button onClick={handleAddPlayer} disabled={addingPlayer}>
                {addingPlayer ? tPlayer("adding") : tPlayer("add")}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* MyPage Dialogs */}
        <ConfirmDialog
          open={logoutDialogOpen}
          onOpenChange={setLogoutDialogOpen}
          title={tAuth("logout")}
          description={tAuth("logoutConfirm")}
          onConfirm={() => {
            setLogoutDialogOpen(false)
            handleLogout()
          }}
          confirmText={tAuth("logout")}
        />

        <ConfirmDialog
          open={deleteAccountDialogOpen}
          onOpenChange={setDeleteAccountDialogOpen}
          title={tMyPage("deleteAccount")}
          description={tMyPage("deleteAccountConfirm")}
          onConfirm={() => {
            setDeleteAccountDialogOpen(false)
            handleDeleteAccount()
          }}
          variant="destructive"
        />

        <ConfirmDialog
          open={removePlayerDialog.open}
          onOpenChange={(open) => setRemovePlayerDialog({ open, username: "", platform: null })}
          title={tPlayer("removePlayer")}
          description={tPlayer("removeConfirm")}
          onConfirm={() => {
            handleRemovePlayer(removePlayerDialog.username, removePlayerDialog.platform)
            setRemovePlayerDialog({ open: false, username: "", platform: null })
          }}
          variant="destructive"
        />

        {/* Change Password Dialog */}
        <Dialog 
          open={changePasswordDialogOpen} 
          onOpenChange={(open) => {
            setChangePasswordDialogOpen(open)
            if (!open) {
              setOldPassword("")
              setNewPassword("")
              setNewPasswordConfirm("")
              setShowOldPassword(false)
              setShowNewPassword(false)
              setShowNewPasswordConfirm(false)
              setPasswordError(null)
            }
          }}
        >
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{tAuth("changePasswordTitle")}</DialogTitle>
              <DialogDescription>{tAuth("changePasswordDescription")}</DialogDescription>
            </DialogHeader>

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="old-password">{tAuth("oldPassword")}</Label>
                <div className="relative">
                  <Input
                    id="old-password"
                    type={showOldPassword ? "text" : "password"}
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                    disabled={changingPassword}
                    autoComplete="current-password"
                    className="pr-10"
                  />
                  <button
                    type="button"
                    onClick={() => setShowOldPassword((prev) => !prev)}
                    aria-label={showOldPassword ? tAuth("hidePassword") : tAuth("showPassword")}
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showOldPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="new-password">{tAuth("newPassword")}</Label>
                <div className="relative">
                  <Input
                    id="new-password"
                    type={showNewPassword ? "text" : "password"}
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    disabled={changingPassword}
                    autoComplete="new-password"
                    className="pr-10"
                  />
                  <button
                    type="button"
                    onClick={() => setShowNewPassword((prev) => !prev)}
                    aria-label={showNewPassword ? tAuth("hidePassword") : tAuth("showPassword")}
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showNewPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="new-password-confirm">{tAuth("newPasswordConfirm")}</Label>
                <div className="relative">
                  <Input
                    id="new-password-confirm"
                    type={showNewPasswordConfirm ? "text" : "password"}
                    value={newPasswordConfirm}
                    onChange={(e) => setNewPasswordConfirm(e.target.value)}
                    disabled={changingPassword}
                    autoComplete="new-password"
                    className="pr-10"
                  />
                  <button
                    type="button"
                    onClick={() => setShowNewPasswordConfirm((prev) => !prev)}
                    aria-label={showNewPasswordConfirm ? tAuth("hidePassword") : tAuth("showPassword")}
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  >
                    {showNewPasswordConfirm ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              {passwordError && (
                <p className="text-sm text-destructive">{passwordError}</p>
              )}
            </div>

            <DialogFooter>
              <Button
                variant="outline"
                onClick={() => setChangePasswordDialogOpen(false)}
                disabled={changingPassword}
              >
                {tCommon("cancel")}
              </Button>
              <Button
                onClick={handleChangePasswordSubmit}
                disabled={changingPassword}
              >
                {changingPassword ? tAuth("changing") : tCommon("confirm")}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </main>
    </div>
  )
}



function LoadingSkeleton() {
  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-4">
        <Skeleton className="h-5 w-32" />
        <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-20 rounded-lg" />
          ))}
        </div>
        <Skeleton className="h-10 rounded-lg" />
      </div>
      <div className="flex flex-col gap-2 sm:flex-row sm:justify-between">
        <Skeleton className="h-10 w-64" />
        <div className="flex gap-2">
          <Skeleton className="h-10 w-56" />
          <Skeleton className="h-10 w-40" />
        </div>
      </div>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {Array.from({ length: 9 }).map((_, i) => (
          <Skeleton key={i} className="h-32 rounded-lg" />
        ))}
      </div>
    </div>
  )
}
