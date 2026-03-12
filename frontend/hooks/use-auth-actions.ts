"use client"

import { useState, useCallback } from "react"
import { logout, changePassword, deleteAccount } from "@/lib/api/auth"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { useTranslations } from "next-intl"
import { useRouter } from "@/i18n/navigation"

export function useAuthActions() {
  const router = useRouter()
  const tAuth = useTranslations("auth")
  const tMyPage = useTranslations("myPage")

  const [loggingOut, setLoggingOut] = useState(false)
  const [changingPassword, setChangingPassword] = useState(false)
  const [passwordError, setPasswordError] = useState<string | null>(null)
  const [deletingAccount, setDeletingAccount] = useState(false)

  const handleLogout = useCallback(async () => {
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
  }, [router, tAuth])

  const handleDeleteAccount = useCallback(async () => {
    if (deletingAccount) return
    try {
      setDeletingAccount(true)
      await deleteAccount()
      toast.success(tMyPage("deleteAccountSuccess"))
      router.replace("/login")
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(err.response?.data?.message || tMyPage("deleteAccountFailed"))
      } else {
        toast.error(tMyPage("deleteAccountFailed"))
      }
      console.error(err)
    } finally {
      setDeletingAccount(false)
    }
  }, [deletingAccount, router, tMyPage])

  const handleChangePasswordSubmit = useCallback(async (
    oldPassword: string,
    newPassword: string,
    newPasswordConfirm: string,
    onSuccess: () => void
  ) => {
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
      onSuccess()
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
  }, [tAuth])

  const clearPasswordError = useCallback(() => setPasswordError(null), [])

  return {
    loggingOut,
    changingPassword,
    passwordError,
    deletingAccount,
    handleLogout,
    handleDeleteAccount,
    handleChangePasswordSubmit,
    clearPasswordError,
  }
}
