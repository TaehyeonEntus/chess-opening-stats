"use client"

import { useState, useCallback } from "react"
import { provideAddPlayer } from "@/lib/provide/provideFacade"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { useTranslations } from "next-intl"
import type { Platform } from "@/lib/types"

/**
 * 플레이어 추가(연동) 관련 로직을 담당하는 훅입니다.
 * 동기화 처리는 useAuthActions의 handleSyncGames로 위임합니다.
 */
export function usePlayer() {
  const tPlayer = useTranslations("player")

  const [addingPlayer, setAddingPlayer] = useState(false)
  const [addPlayerError, setAddPlayerError] = useState<string | null>(null)

  const handleAddPlayer = useCallback(async (
    username: string,
    platform: Platform,
    onSuccess: (playersAfterAdd?: any) => void // 추가 성공 후 콜백 (여기서 handleSyncGames를 호출하도록 유도)
  ) => {
    if (!username.trim()) {
      setAddPlayerError(tPlayer("enterUsername"))
      return
    }

    try {
      setAddingPlayer(true)
      setAddPlayerError(null)
      // 1. 플레이어 추가 API 호출
      await provideAddPlayer({ username: username.trim(), platform })
      
      toast.success(tPlayer("addSuccess"))
      
      // 2. 성공 시 콜백 실행 (UI 레이어에서 handleSyncGames를 호출하여 폴링 시작)
      onSuccess()
      
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
  }, [tPlayer])

  const clearAddPlayerError = useCallback(() => setAddPlayerError(null), [])

  return {
    addingPlayer,
    addPlayerError,
    handleAddPlayer,
    clearAddPlayerError,
  }
}
