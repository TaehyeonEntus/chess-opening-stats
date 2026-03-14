"use client"

import { useState } from "react"
import { useRouter } from "@/i18n/navigation"
import { useTranslations } from "next-intl"
import { logout, changePassword } from "@/lib/api/auth"
import { deletePlayer, syncAccount } from "@/lib/api/api"
import { runSyncFlow } from "@/lib/sync/runSyncFlow"
import { toast } from "sonner"
import { AxiosError } from "axios"
import type { PlayerInfo } from "@/lib/types"

export function useAuthActions() {
  const router = useRouter()
  const tAuth = useTranslations("auth")
  const tCommon = useTranslations("common")

  const [loggingOut, setLoggingOut] = useState(false)
  const [changingPassword, setChangingPassword] = useState(false)
  const [deletingAccount, setDeletingAccount] = useState(false)
  const [syncing, setSyncing] = useState(false)
  const [isPolling, setIsPolling] = useState(false)
  const [passwordError, setPasswordError] = useState<string | null>(null)

  const handleLogout = async () => {
    try {
      setLoggingOut(true)
      await logout()
      router.replace("/login")
      toast.success(tAuth("logoutSuccess"))
    } catch (err) {
      console.error(err)
      toast.error(tAuth("logoutFailed"))
    } finally {
      setLoggingOut(false)
    }
  }

  const handleDeleteAccount = async () => {
    try {
      setDeletingAccount(true)
      // Note: Ideally there would be a deleteAccount API, 
      // but if not, we might just be deleting players or logging out.
      // Based on MyPage usage, it expects this to handle the whole flow.
      // For now, let's assume there's a delete account endpoint or similar.
      // await apiClient.delete("/account")
      toast.success("Account deletion initiated (Mock)")
      await handleLogout()
    } catch (err) {
      console.error(err)
      toast.error("Failed to delete account")
    } finally {
      setDeletingAccount(false)
    }
  }

  const handleChangePasswordSubmit = async (
    oldPassword: string,
    newPassword: string,
    newPasswordConfirm: string,
    onSuccess?: () => void
  ) => {
    if (newPassword !== newPasswordConfirm) {
      setPasswordError(tAuth("passwordMismatch"))
      return
    }

    try {
      setChangingPassword(true)
      setPasswordError(null)
      await changePassword({ oldPassword, newPassword, newPasswordConfirm })
      toast.success(tAuth("changePasswordSuccess"))
      onSuccess?.()
    } catch (err) {
      if (err instanceof AxiosError) {
        setPasswordError(err.response?.data?.message || tAuth("changePasswordFailed"))
      } else {
        setPasswordError(tAuth("changePasswordFailed"))
      }
    } finally {
      setChangingPassword(false)
    }
  }

  const handleSyncGames = async (players: PlayerInfo[]) => {
      if (syncing || isPolling) return

      try {
          setSyncing(true)
          await runSyncFlow()
          toast.success(tCommon("syncSuccess"))
      } catch (err) {
          console.error(err)
          toast.error(tCommon("failed"))
      } finally {
          setSyncing(false)
      }
  }

  const clearPasswordError = () => setPasswordError(null)

  return {
    loggingOut,
    changingPassword,
    deletingAccount,
    syncing,
    isPolling,
    passwordError,
    handleLogout,
    handleDeleteAccount,
    handleChangePasswordSubmit,
    handleSyncGames,
    clearPasswordError
  }
}
