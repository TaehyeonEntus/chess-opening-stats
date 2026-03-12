"use client"

import { useState, useCallback } from "react"
import { provideAddPlayer } from "@/lib/provide/provideFacade"
import { runSyncFlow } from "@/lib/sync/runSyncFlow"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { useTranslations } from "next-intl"
import type { Platform } from "@/lib/types"

export function usePlayer() {
  const tPlayer = useTranslations("player")
  const t = useTranslations("home")

  const [addingPlayer, setAddingPlayer] = useState(false)
  const [addPlayerError, setAddPlayerError] = useState<string | null>(null)
  const [syncing, setSyncing] = useState(false)
  const [syncError, setSyncError] = useState<string | null>(null)

  const handleSync = useCallback(async (onSuccess?: () => void) => {
    try {
      setSyncing(true)
      setSyncError(null)
      await runSyncFlow()
      onSuccess?.()
      window.location.reload()
    } catch (err) {
      console.error("Failed to sync account players", err)
      setSyncError(t("syncFailedTryAgain"))
    } finally {
      setSyncing(false)
    }
  }, [t])

  const handleAddPlayer = useCallback(async (
    username: string,
    platform: Platform,
    onSuccess: () => void
  ) => {
    if (!username.trim()) {
      setAddPlayerError(tPlayer("enterUsername"))
      return
    }

    try {
      setAddingPlayer(true)
      setAddPlayerError(null)
      await provideAddPlayer({ username: username.trim(), platform })
      onSuccess()
      toast.success(tPlayer("addSuccess"))
      await handleSync()
    } catch (err) {
      console.error("Failed to add player", err)
      if (err instanceof AxiosError) {
        const status = err.response?.status
        const responseData = err.response?.data as { message?: string; code?: unknown } | string | undefined
        const responseCode =
          typeof responseData === "object" && responseData !== null && "code" in responseData
            ? String(responseData.code ?? "")
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
  }, [tPlayer, handleSync])

  const clearAddPlayerError = useCallback(() => setAddPlayerError(null), [])

  return {
    addingPlayer,
    addPlayerError,
    syncing,
    syncError,
    handleSync,
    handleAddPlayer,
    clearAddPlayerError,
  }
}
