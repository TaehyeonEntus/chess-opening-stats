"use client"

import { useState } from "react"
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
import type { Platform } from "@/lib/types"

export default function HomePage() {
  const t = useTranslations("home")
  const tAuth = useTranslations("auth")
  const tPlayer = useTranslations("player")
  const tMyPage = useTranslations("myPage")

  // Core data hook
  const {
    allOpenings,
    filteredAndSortedOpenings,
    currentSummary,
    nickname,
    loading,
    error,
    loadData,
    colorFilter, setColorFilter,
    sortBy, setSortBy,
    minGames, setMinGames,
    maxGames, setMaxGames,
    search, setSearch,
  } = useOpeningData()

  // Auth actions hook
  const {
    loggingOut,
    changingPassword,
    passwordError,
    handleLogout,
    handleDeleteAccount,
    handleChangePasswordSubmit,
    clearPasswordError,
  } = useAuthActions()

  // Player hook
  const { addingPlayer, addPlayerError, syncing, handleAddPlayer, handleSync, clearAddPlayerError } = usePlayer()

  // Account hook (depends on player changes refreshing data)
  const { accountInfo, loadingAccountInfo, loadAccountInfo, handleRemovePlayer } =
    useAccount(() => loadData(false))

  // UI state
  const [myPageOpen, setMyPageOpen] = useState(false)
  const [addPlayerOpen, setAddPlayerOpen] = useState(false)
  const [changePasswordOpen, setChangePasswordOpen] = useState(false)
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false)
  const [deleteAccountDialogOpen, setDeleteAccountDialogOpen] = useState(false)
  const [removePlayerDialog, setRemovePlayerDialog] = useState<{
    open: boolean; username: string; platform: Platform | null
  }>({ open: false, username: "", platform: null })

  function handleSyncFromMyPage() {
    setMyPageOpen(false)
    handleSync()
  }

  return (
    <div className="min-h-screen overflow-x-hidden bg-background">
      <main className="mx-auto max-w-screen-2xl px-4 py-6 lg:px-6">
        <HeaderBar
          loading={loading}
          nickname={nickname}
          onMyPageOpen={() => setMyPageOpen(true)}
        />

        {loading ? (
          <LoadingSkeleton />
        ) : error ? (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
            {error}
          </div>
        ) : allOpenings.length === 0 ? (
          <EmptyState
            onAddPlayer={() => setAddPlayerOpen(true)}
            disabled={syncing || addingPlayer}
          />
        ) : (
          <div className="flex flex-col gap-6">
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
          syncing={syncing}
          onLoadAccountInfo={loadAccountInfo}
          onSync={handleSyncFromMyPage}
          onAddPlayer={() => setAddPlayerOpen(true)}
          onRemovePlayer={(username, platform) =>
            setRemovePlayerDialog({ open: true, username, platform })
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
          onAdd={handleAddPlayer}
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
          onOpenChange={(open) => setRemovePlayerDialog({ open, username: "", platform: null })}
          title={tPlayer("removePlayer")}
          description={tPlayer("removeConfirm")}
          onConfirm={() => {
            handleRemovePlayer(removePlayerDialog.username, removePlayerDialog.platform)
            setRemovePlayerDialog({ open: false, username: "", platform: null })
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
