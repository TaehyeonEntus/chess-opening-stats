"use client"

import { useEffect } from "react"
import { healthCheck } from "@/lib/api/api"

/**
 * 전역 세션 및 CSRF(XSRF) 토큰 핸드셰이크를 담당하는 컴포넌트.
 */
export function AuthInitializer() {
  useEffect(() => {
    // 1. 이미 XSRF-TOKEN 쿠키가 있는지 확인
    const hasCsrfToken = document.cookie.split(";").some((item) => item.trim().startsWith("XSRF-TOKEN="))

    // 2. 토큰이 없을 때만 백엔드와 핸드셰이크 수행
    if (!hasCsrfToken) {
      healthCheck().catch(() => {
        // 비로그인 상태라도 쿠키(JSESSIONID, XSRF-TOKEN)는 구워집니다.
      })
    }
  }, [])

  return null
}
