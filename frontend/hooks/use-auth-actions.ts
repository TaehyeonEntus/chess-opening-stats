"use client"

import { useState, useCallback, useTransition } from "react"
import { logout, changePassword, deleteAccount, syncGames } from "@/lib/api/auth"
import { toast } from "sonner"
import { AxiosError } from "axios"
import { useTranslations } from "next-intl"
import { useRouter } from "@/i18n/navigation"
import { SyncGameResponse, PlayerInfo } from "@/lib/types"

export function useAuthActions() {
  const router = useRouter()
  const tAuth = useTranslations("auth")
  const tMyPage = useTranslations("myPage")
  const [isPending, startTransition] = useTransition()

  const [loading, setLoading] = useState({
    logout: false,
    changePassword: false,
    deleteAccount: false,
    sync: false,
  })
  
  const [isPolling, setIsPolling] = useState(false)
  const [passwordError, setPasswordError] = useState<string | null>(null)

  // 보안 세션을 완전히 정리하고 로그인 페이지로 하드 리다이렉트
  const performHardLogout = useCallback(() => {
    if (typeof window !== "undefined") {
      const cookieOptions = `; path=/; domain=${process.env.NEXT_PUBLIC_COOKIE_DOMAIN || ''}; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
      document.cookie = `XSRF-TOKEN=${cookieOptions}`;
      document.cookie = `JSESSIONID=${cookieOptions}`;
      window.location.href = "/api/auth/clear";
    }
  }, [])

  const handleLogout = useCallback(async () => {
    setLoading(prev => ({ ...prev, logout: true }))
    try {
      await logout()
      toast.success(tAuth("logoutSuccess"))
      performHardLogout()
    } catch (err) {
      console.error("Failed to logout", err)
      performHardLogout()
    } finally {
      setLoading(prev => ({ ...prev, logout: false }))
    }
  }, [tAuth, performHardLogout])

  const handleDeleteAccount = useCallback(async () => {
    if (loading.deleteAccount) return
    setLoading(prev => ({ ...prev, deleteAccount: true }))
    try {
      await deleteAccount()
      toast.success(tMyPage("deleteAccountSuccess"))
      performHardLogout()
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(err.response?.data?.message || tMyPage("deleteAccountFailed"))
      } else {
        toast.error(tMyPage("deleteAccountFailed"))
      }
      console.error("Failed to delete account", err)
    } finally {
      setLoading(prev => ({ ...prev, deleteAccount: false }))
    }
  }, [loading.deleteAccount, tMyPage, performHardLogout])

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
      setLoading(prev => ({ ...prev, changePassword: true }))
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
      setLoading(prev => ({ ...prev, changePassword: false }))
    }
  }, [tAuth])

  const handleSyncGames = useCallback(async (currentPlayers: PlayerInfo[] = []) => {
    if (loading.sync || isPolling) return
    
    setLoading(prev => ({ ...prev, sync: true }))
    try {
      // 1. 갱신 요청 (갱신을 시작한다는 의미)
      const result: SyncGameResponse = await syncGames()
      
      // 2. 응답으로 받은 큐 대기 사용자 수 계산
      const totalQueued = Object.values(result).reduce((acc, curr) => acc + (typeof curr === 'number' ? curr : 0), 0)
      
      // 3. 요청 성공 알림 (사용자 대기열 정보 표시)
      toast.info(tMyPage("syncSuccess", { count: totalQueued }))
      
      // 4. 폴링 시작: accounts/me/sync/status가 false가 될 때까지 감시
      setIsPolling(true)
      let pollCount = 0
      const maxPolls = 60 // 최대 3분 (3초 * 60)
      
      const poll = async () => {
        try {
          const { fetchSyncStatus } = await import("@/lib/api/api")
          const syncStatus = await fetchSyncStatus()
          
          // status가 false이면 동기화 완료
          if (syncStatus.status === false) {
            toast.success(tMyPage("syncComplete"))
            setIsPolling(false)
            
            // 데이터 갱신을 위해 전체 페이지 새로고침
            if (typeof window !== "undefined") {
                window.location.reload()
            }
            return
          }
          
          if (pollCount < maxPolls) {
            pollCount++
            setTimeout(poll, 3000)
          } else {
            setIsPolling(false)
            toast.error(tMyPage("syncTimeout"))
          }
        } catch (e) {
          // 폴링 중 에러가 발생해도 일단 중단 (네트워크 오류 등)
          setIsPolling(false)
          console.error("Polling failed", e)
        }
      }
      
      // 약간의 지연 후 첫 폴링 시작
      setTimeout(poll, 3000)
      
    } catch (err) {
      console.error(err)
      if (err instanceof AxiosError && err.response?.status === 429) {
        toast.error(tMyPage("syncRateLimit"))
      } else {
        toast.error(tMyPage("syncFailed"))
      }
    } finally {
      setLoading(prev => ({ ...prev, sync: false }))
    }
  }, [loading.sync, isPolling, router, tMyPage])

  const clearPasswordError = useCallback(() => setPasswordError(null), [])

  return {
    loggingOut: loading.logout,
    changingPassword: loading.changePassword,
    deletingAccount: loading.deleteAccount,
    syncing: loading.sync,
    isPolling,
    isPending,
    passwordError,
    handleLogout,
    handleDeleteAccount,
    handleChangePasswordSubmit,
    handleSyncGames,
    clearPasswordError,
  }
}
