import type { Opening, OpeningResult } from "./types"
import { OPENINGS_MASTER, GLOBAL_RESULTS, PERSONAL_RESULTS } from "./dummy-data"

// API 추상화 레이어
// 현재는 더미 데이터 반환, 나중에 Java 백엔드 API로 교체

const USE_DUMMY = true
// const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080"

export async function fetchOpeningMaster(): Promise<Opening[]> {
  if (USE_DUMMY) {
    return OPENINGS_MASTER
  }

  // TODO: 백엔드 API로 교체
  // const res = await fetch(`${BACKEND_URL}/api/openings/master`)
  // return res.json()
  return []
}

export async function fetchOpeningResults(playerId?: string): Promise<OpeningResult[]> {
  if (USE_DUMMY) {
    // playerId가 있으면 개인 데이터, 없으면 전체 데이터
    return playerId ? PERSONAL_RESULTS : GLOBAL_RESULTS
  }

  // TODO: 백엔드 API로 교체
  // const url = playerId
  //   ? `${BACKEND_URL}/api/openings/stats?playerId=${playerId}`
  //   : `${BACKEND_URL}/api/openings/stats`
  // const res = await fetch(url)
  // return res.json()
  return []
}
