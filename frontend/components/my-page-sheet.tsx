"use client"

import { useEffect } from "react"
import { User, RefreshCw, KeyRound, LogOut, Trash2, Loader2 } from "lucide-react"
import { useTranslations, useLocale } from "next-intl"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet"
import type { AccountInfoResponse, Platform } from "@/lib/types"

interface MyPageSheetProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  accountInfo: AccountInfoResponse | null
  loadingAccountInfo: boolean
  syncing: boolean
  onLoadAccountInfo: () => void
  onSync: () => void
  onAddPlayer: () => void
  onRemovePlayer: (playerId: number) => void
  onChangePassword: () => void
  onLogout: () => void
  onDeleteAccount: () => void
}

function formatDateTime(isoString: string, locale: string) {
  const date = new Date(isoString)
  return date.toLocaleString(locale === "ko" ? "ko-KR" : "en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  })
}

export function MyPageSheet({
  open,
  onOpenChange,
  accountInfo,
  loadingAccountInfo,
  syncing,
  onLoadAccountInfo,
  onSync,
  onAddPlayer,
  onRemovePlayer,
  onChangePassword,
  onLogout,
  onDeleteAccount,
}: MyPageSheetProps) {
  const t = useTranslations("home")
  const tAuth = useTranslations("auth")
  const tPlayer = useTranslations("player")
  const tMyPage = useTranslations("myPage")
  const locale = useLocale()

  useEffect(() => {
    if (open && !accountInfo) {
      onLoadAccountInfo()
    }
  }, [open]) // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-xl overflow-y-auto">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            {t("myPage")}
          </SheetTitle>
          <SheetDescription>{tMyPage("accountSettings")}</SheetDescription>
        </SheetHeader>

        {loadingAccountInfo ? (
          <div className="flex flex-col items-center justify-center py-12 gap-4">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <p className="text-sm text-muted-foreground">{tMyPage("loadingAccountInfo")}</p>
          </div>
        ) : accountInfo ? (
          <div className="mt-6 space-y-6">
            {/* Profile */}
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

            {/* Data Sync */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base">
                  <RefreshCw className="h-4 w-4" />
                  {tMyPage("dataSync")}
                </CardTitle>
                <CardDescription className="text-xs">
                  {tMyPage("lastSync")}:{" "}
                  {accountInfo.lastSyncedAt
                    ? formatDateTime(accountInfo.lastSyncedAt, locale)
                    : tMyPage("noSyncRecord")}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={onSync} disabled={syncing} className="w-full" size="sm">
                  {syncing ? tMyPage("syncing") : tMyPage("syncNow")}
                </Button>
              </CardContent>
            </Card>

            {/* Linked Players */}
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
                            <Badge
                              variant={player.platform === "chess_com" ? "default" : "secondary"}
                              className="text-xs"
                            >
                              {player.platform === "chess_com" ? "Chess.com" : "Lichess"}
                            </Badge>
                          </div>
                          <p className="text-xs text-muted-foreground mt-1">
                            {tPlayer("lastPlayed")}: {formatDateTime(player.lastPlayedAt, locale)}
                          </p>
                        </div>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => onRemovePlayer(player.id)}
                          className="text-destructive text-xs hover:text-destructive"
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
                  onClick={onAddPlayer}
                  size="sm"
                >
                  {tPlayer("addPlayer")}
                </Button>
              </CardContent>
            </Card>

            {/* Account Settings */}
            <Card>
              <CardHeader>
                <CardTitle className="text-base">{tMyPage("accountSettings")}</CardTitle>
                <CardDescription className="text-xs">{tMyPage("accountSettings")}</CardDescription>
              </CardHeader>
              <CardContent className="space-y-2">
                <Button
                  variant="outline"
                  className="w-full justify-start gap-2"
                  onClick={onChangePassword}
                  size="sm"
                >
                  <KeyRound className="h-4 w-4" />
                  {tMyPage("changePassword")}
                </Button>
                <Separator />
                <Button
                  variant="outline"
                  className="w-full justify-start gap-2"
                  onClick={onLogout}
                  size="sm"
                >
                  <LogOut className="h-4 w-4" />
                  {tAuth("logout")}
                </Button>
                <Separator />
                <Button
                  variant="destructive"
                  className="w-full justify-start gap-2"
                  onClick={onDeleteAccount}
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
  )
}
