"use client"

import { useEffect } from "react"
import { fetchAccountInfo } from "@/lib/api/api"

/**
 * 전역 세션 및 CSRF(XSRF) 토큰 핸드셰이크를 담당하는 컴포넌트.
 */
export function AuthInitializer() {
  useEffect(() => {
    // 1. 이미 XSRF-TOKEN 쿠키가 있는지 확인
    const hasCsrfToken = document.cookie.split(";").some((item) => item.trim().startsWith("XSRF-TOKEN="))

    // 2. 토큰이 없을 때만 백엔드와 핸드셰이크 수행
    // /health는 보안 필터를 거치지 않을 수 있어, 세션 연결이 확실한 /accounts/me를 사용합니다.
    if (!hasCsrfToken) {
      fetchAccountInfo().catch(() => {
        // 비로그인 상태면 401이 나겠지만, 쿠키(JSESSIONID, XSRF-TOKEN)는 구워집니다.
      })
    }
  }, [])

  return null
}
