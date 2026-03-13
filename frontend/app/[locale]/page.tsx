"use client"

import { useState, useEffect, useCallback } from "react"
import { useTranslations } from "next-intl"
import { OpeningGrid } from "@/components/opening-grid"
import { OpeningSummary } from "@/components/opening-summary"
import { OpeningFilter } from "@/components/opening-filter"
import { Skeleton } from "@/components/ui/skeleton"
import { HeaderBar } from "@/components/header-bar"
import { MyPageSheet } from "@/components/my-page-sheet"
import { AddPlayerDialog } from "@/components/add-player-dialog"
import { ChangePasswordDialog } from "@/components/change-password-dialog"
import { EmptyState } from "@/components/empty-state"
import { ConfirmDialog } from "@/components/confirm-dialog"
import { useOpeningData } from "@/hooks/use-opening-data"
import { useAccount } from "@/hooks/use-account"
import { useAuthActions } from "@/hooks/use-auth-actions"
import { usePlayer } from "@/hooks/use-player"
import { RefreshCw, Info } from "lucide-react"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

export default function HomePage() {
  const t = useTranslations("home")
  const tAuth = useTranslations("auth")
  const tPlayer = useTranslations("player")
  const tMyPage = useTranslations("myPage")

  // 1. Core data hook
  const {
    allOpenings,
    epdMap,
    filteredAndSortedOpenings,
    currentSummary,
    nickname,
    hasPlayers,
    loading: loadingData,
    error,
    loadData,
    colorFilter, setColorFilter,
    sortBy, setSortBy,
    minGames, setMinGames,
    maxGames, setMaxGames,
    search, setSearch,
  } = useOpeningData()

  // 2. Auth & Sync actions hook
  const {
    handleLogout,
    handleDeleteAccount,
    handleChangePasswordSubmit,
    handleSyncGames,
    clearPasswordError,
    changingPassword,
    syncing,
    isPolling,
    passwordError,
  } = useAuthActions()

  // 3. Player hook
  const { addingPlayer, addPlayerError, handleAddPlayer, clearAddPlayerError } = usePlayer()

  // 4. Account hook (Depends on player changes refreshing data)
  const { accountInfo, loadingAccountInfo, loadAccountInfo, handleRemovePlayer } =
    useAccount(() => loadData(false))

  // UI state
  const [myPageOpen, setMyPageOpen] = useState(false)
  const [addPlayerOpen, setAddPlayerOpen] = useState(false)
  const [changePasswordOpen, setChangePasswordOpen] = useState(false)
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false)
  const [deleteAccountDialogOpen, setDeleteAccountDialogOpen] = useState(false)
  const [removePlayerDialog, setRemovePlayerDialog] = useState<{
    open: boolean; playerId: number | null
  }>({ open: false, playerId: null })

  // 데이터 동기화 완료 전까지의 로딩 상태 정의
  // (플레이어는 있지만 데이터가 0개이며, 현재 폴링/동기화 중인 경우)
  const isSyncInProgress = syncing || isPolling
  const isInitialSyncing = allOpenings.length === 0 && hasPlayers && isSyncInProgress

  const handleSyncFromMyPage = useCallback(() => {
    if (accountInfo) {
      handleSyncGames(accountInfo.players)
    }
  }, [accountInfo, handleSyncGames])

  const onAddPlayerSuccess = useCallback(() => {
    setAddPlayerOpen(false)
    // 플레이어가 추가된 직후의 정보를 서버에서 다시 가져온 뒤 동기화 폴링 시작
    loadAccountInfo().then((newAccountInfo) => {
        if (newAccountInfo) {
            handleSyncGames(newAccountInfo.players)
            loadData(false)
        }
    })
  }, [handleSyncGames, loadAccountInfo, loadData])

  return (
    <div className="min-h-screen overflow-x-hidden bg-background">
      <main className="mx-auto max-w-screen-2xl px-4 py-6 lg:px-6">
        <HeaderBar
          loading={loadingData || isSyncInProgress}
          nickname={nickname}
          onMyPageOpen={() => setMyPageOpen(true)}
        />

        {loadingData && !isInitialSyncing ? (
          <LoadingSkeleton />
        ) : error ? (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
            {error}
          </div>
        ) : allOpenings.length === 0 && !hasPlayers ? (
          <EmptyState
            onAddPlayer={() => setAddPlayerOpen(true)}
            disabled={isSyncInProgress || addingPlayer}
          />
        ) : isInitialSyncing ? (
          <div className="flex flex-col items-center justify-center py-32 text-center">
            <div className="relative mb-6">
              <div className="absolute inset-0 animate-ping rounded-full bg-primary/20" />
              <div className="relative flex h-16 w-16 items-center justify-center rounded-full bg-primary/10 text-primary">
                <RefreshCw className="h-8 w-8 animate-spin" />
              </div>
            </div>
            <p className="text-sm text-muted-foreground animate-pulse">
              {t("syncTip")}
            </p>
          </div>
        ) : (
          <div className="flex flex-col gap-6">
            {isSyncInProgress && (
              <Alert className="bg-primary/5 border-primary/20 animate-pulse">
                <RefreshCw className="h-4 w-4 animate-spin text-primary" />
                <AlertTitle className="text-primary font-semibold">{t("syncInProgress")}</AlertTitle>
                <AlertDescription>
                  데이터를 동기화하고 있습니다. 완료될 때까지 잠시만 기다려 주세요.
                </AlertDescription>
              </Alert>
            )}

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
              epdMap={epdMap}
              search={search}
              onSearchChange={setSearch}
            />

            {currentSummary && <OpeningSummary summary={currentSummary} colorFilter={colorFilter} />}
            <OpeningGrid stats={filteredAndSortedOpenings} />
          </div>
        )}

        {/* Dialogs & Sheets */}
        <MyPageSheet
          open={myPageOpen}
          onOpenChange={setMyPageOpen}
          accountInfo={accountInfo}
          loadingAccountInfo={loadingAccountInfo}
          syncing={isSyncInProgress}
          onLoadAccountInfo={loadAccountInfo}
          onSync={handleSyncFromMyPage}
          onAddPlayer={() => setAddPlayerOpen(true)}
          onRemovePlayer={(playerId) =>
            setRemovePlayerDialog({ open: true, playerId })
          }
          onChangePassword={() => setChangePasswordOpen(true)}
          onLogout={() => setLogoutDialogOpen(true)}
          onDeleteAccount={() => setDeleteAccountDialogOpen(true)}
        />

        <AddPlayerDialog
          open={addPlayerOpen}
          onOpenChange={setAddPlayerOpen}
          addingPlayer={addingPlayer}
          addPlayerError={addPlayerError}
          onAdd={(username, platform) => handleAddPlayer(username, platform, onAddPlayerSuccess)}
          onClearError={clearAddPlayerError}
        />

        <ChangePasswordDialog
          open={changePasswordOpen}
          onOpenChange={setChangePasswordOpen}
          changingPassword={changingPassword}
          passwordError={passwordError}
          onSubmit={handleChangePasswordSubmit}
          onClearError={clearPasswordError}
        />

        <ConfirmDialog
          open={logoutDialogOpen}
          onOpenChange={setLogoutDialogOpen}
          title={tAuth("logout")}
          description={tAuth("logoutConfirm")}
          onConfirm={() => { setLogoutDialogOpen(false); handleLogout() }}
          confirmText={tAuth("logout")}
        />

        <ConfirmDialog
          open={deleteAccountDialogOpen}
          onOpenChange={setDeleteAccountDialogOpen}
          title={tMyPage("deleteAccount")}
          description={tMyPage("deleteAccountConfirm")}
          onConfirm={() => { setDeleteAccountDialogOpen(false); handleDeleteAccount() }}
          variant="destructive"
        />

        <ConfirmDialog
          open={removePlayerDialog.open}
          onOpenChange={(open) => setRemovePlayerDialog({ open, playerId: null })}
          title={tPlayer("removePlayer")}
          description={tPlayer("removeConfirm")}
          onConfirm={() => {
            if (removePlayerDialog.playerId !== null) {
              handleRemovePlayer(removePlayerDialog.playerId)
            }
            setRemovePlayerDialog({ open: false, playerId: null })
          }}
          variant="destructive"
        />
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
