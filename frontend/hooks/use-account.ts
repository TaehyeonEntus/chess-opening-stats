"use client"

import { useState, useCallback } from "react"
import { deletePlayer } from "@/lib/api/api"
import { provideHomeView } from "@/lib/provide/provideFacade"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { useTranslations } from "next-intl"
import type { AccountInfoResponse, Platform } from "@/lib/types"

export function useAccount(onPlayerChange?: () => Promise<void>) {
  const tMyPage = useTranslations("myPage")
  const tPlayer = useTranslations("player")

  const [accountInfo, setAccountInfo] = useState<AccountInfoResponse | null>(null)
  const [loadingAccountInfo, setLoadingAccountInfo] = useState(false)
  const [deletingAccount, setDeletingAccount] = useState(false)
  const [removingPlayer, setRemovingPlayer] = useState(false)

  const loadAccountInfo = useCallback(async () => {
    try {
      setLoadingAccountInfo(true)
      const homeView = await provideHomeView()
      const newAccountInfo: AccountInfoResponse = {
        nickname: homeView.account.nickname,
        lastSyncedAt: homeView.account.lastSyncedAt,
        players: homeView.players
      }
      setAccountInfo(newAccountInfo)
      return newAccountInfo
    } catch (err) {
      console.error("Failed to load account info", err)
      toast.error(tMyPage("failedToLoadAccountInfo"))
      return null
    } finally {
      setLoadingAccountInfo(false)
    }
  }, [tMyPage])

  const handleRemovePlayer = useCallback(async (playerId: number) => {
    if (removingPlayer) return
    try {
      setRemovingPlayer(true)
      await deletePlayer(playerId)
      toast.success(tPlayer("removeSuccess"))
      await loadAccountInfo()
      await onPlayerChange?.()
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(err.response?.data?.message || tPlayer("removeFailed"))
      } else {
        toast.error(tPlayer("removeFailed"))
      }
      console.error(err)
    } finally {
      setRemovingPlayer(false)
    }
  }, [removingPlayer, tPlayer, loadAccountInfo, onPlayerChange])

  return {
    accountInfo,
    loadingAccountInfo,
    deletingAccount,
    setDeletingAccount,
    removingPlayer,
    loadAccountInfo,
    handleRemovePlayer,
  }
}
