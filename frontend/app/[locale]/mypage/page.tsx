"use client"

import { useState, useEffect } from "react"
import { useTranslations, useLocale } from "next-intl"
import { Link, useRouter } from "@/i18n/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { ArrowLeft, RefreshCw, Trash2, KeyRound, User, Loader2, LogOut, Eye, EyeOff } from "lucide-react"
import type { Platform, AccountInfoResponse } from "@/lib/types"
import { fetchAccountInfo, logout, changePassword, deleteAccount, deletePlayer } from "@/lib/api/api"
import { runSyncFlow } from "@/lib/sync/runSyncFlow"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { ThemeToggle } from "@/components/theme-toggle"
import { LanguageToggle } from "@/components/language-toggle"
import { ConfirmDialog } from "@/components/confirm-dialog"

export default function MyPage() {
  const router = useRouter()
  const locale = useLocale()
  const t = useTranslations("myPage")
  const tAuth = useTranslations("auth")
  const tHome = useTranslations("home")
  const tPlayer = useTranslations("player")
  const tCommon = useTranslations("common")
  
  const [syncing, setSyncing] = useState(false)
  const [loading, setLoading] = useState(true)
  const [accountInfo, setAccountInfo] = useState<AccountInfoResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  // Dialog states
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

  useEffect(() => {
    loadAccountInfo()
  }, [])

  const loadAccountInfo = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchAccountInfo()
      setAccountInfo(data)
    } catch (err) {
      setError(t("failedToLoadAccountInfo"))
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSync = async () => {
    try {
      setSyncing(true)
      await runSyncFlow()
      window.location.reload()
    } catch (err) {
      toast.error(tHome("syncFailed"))
      console.error(err)
    } finally {
      setSyncing(false)
    }
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

  const handleLogout = async () => {
    try {
      await logout()
      toast.success(tAuth("logoutSuccess"))
      router.push("/")
    } catch (err) {
      toast.error(tAuth("logoutFailed"))
      console.error(err)
    }
  }

  const handleDeleteAccount = async () => {
    if (deletingAccount) {
      return
    }

    try {
      setDeletingAccount(true)
      await deleteAccount()
      toast.success(t("deleteAccountSuccess"))
      router.replace("/login")
    } catch (err) {
      if (err instanceof AxiosError) {
        const message = err.response?.data?.message || t("deleteAccountFailed")
        toast.error(message)
      } else {
        toast.error(t("deleteAccountFailed"))
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

  if (loading) {
    return (
      <div className="min-h-screen overflow-x-hidden bg-background flex items-center justify-center">
        <div className="flex flex-col items-center gap-4">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
          <p className="text-muted-foreground">{t("loadingAccountInfo")}</p>
        </div>
      </div>
    )
  }

  if (error || !accountInfo) {
    return (
      <div className="min-h-screen overflow-x-hidden bg-background flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>{tCommon("error")}</CardTitle>
            <CardDescription>{error || t("failedToLoadAccountInfo")}</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => router.push("/")}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                {t("goHome")}
              </Button>
              <Button onClick={loadAccountInfo}>
                <RefreshCw className="mr-2 h-4 w-4" />
                {t("tryAgain")}
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <>
      <div className="min-h-screen overflow-x-hidden bg-background">
        <div className="absolute top-4 right-4 flex gap-2">
          <ThemeToggle />
          <LanguageToggle />
        </div>
        
        <main className="mx-auto max-w-4xl px-4 py-6 lg:px-6">
          <div className="mb-6">
            <Link href="/">
              <Button variant="ghost" className="gap-2">
                <ArrowLeft className="h-4 w-4" />
                {tCommon("back")}
              </Button>
            </Link>
          </div>

          <div className="space-y-6">
            {/* Profile Card */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <User className="h-8 w-8" />
                    <div>
                      <CardTitle className="text-2xl">{accountInfo.nickname}</CardTitle>
                      <CardDescription>{t("profile")}</CardDescription>
                    </div>
                  </div>
                </div>
              </CardHeader>
            </Card>

            {/* Data Sync Card */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <RefreshCw className="h-5 w-5" />
                  {t("dataSync")}
                </CardTitle>
                <CardDescription>
                  {t("lastSync")}: {accountInfo.lastSyncedAt ? formatDateTime(accountInfo.lastSyncedAt) : t("noSyncRecord")}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={handleSync} disabled={syncing} className="w-full">
                  {syncing ? t("syncing") : t("syncNow")}
                </Button>
              </CardContent>
            </Card>

            {/* Linked Players Card */}
            <Card>
              <CardHeader>
                <CardTitle>{t("linkedAccounts")}</CardTitle>
                <CardDescription>{t("linkedAccounts")}</CardDescription>
              </CardHeader>
              <CardContent>
                {accountInfo.players.length === 0 ? (
                  <p className="text-sm text-muted-foreground text-center py-4">
                    {tPlayer("noLinkedAccounts")}
                  </p>
                ) : (
                  <div className="space-y-3">
                    {accountInfo.players.map((player) => (
                      <div
                        key={player.username}
                        className="flex items-center justify-between rounded-lg border bg-card p-4"
                      >
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <p className="font-semibold">{player.username}</p>
                            <Badge variant={player.platform === "CHESS_COM" ? "default" : "secondary"}>
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
                          className="text-destructive"
                        >
                          {tPlayer("removePlayer")}
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Account Settings Card */}
            <Card>
              <CardHeader>
                <CardTitle>{t("accountSettings")}</CardTitle>
                <CardDescription>{t("accountSettings")}</CardDescription>
              </CardHeader>
              <CardContent className="space-y-3">
                <Button
                  variant="outline"
                  className="w-full justify-start gap-2"
                  onClick={handleChangePassword}
                >
                  <KeyRound className="h-4 w-4" />
                  {t("changePassword")}
                </Button>
                <Separator />
                <Button
                  variant="outline"
                  className="w-full justify-start gap-2"
                  onClick={() => setLogoutDialogOpen(true)}
                >
                  <LogOut className="h-4 w-4" />
                  {tAuth("logout")}
                </Button>
                <Separator />
                <Button
                  variant="destructive"
                  className="w-full justify-start gap-2"
                  onClick={() => setDeleteAccountDialogOpen(true)}
                >
                  <Trash2 className="h-4 w-4" />
                  {t("deleteAccount")}
                </Button>
              </CardContent>
            </Card>
          </div>
        </main>
      </div>

      {/* Dialogs */}
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
        title={t("deleteAccount")}
        description={t("deleteAccountConfirm")}
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
    </>
  )
}
