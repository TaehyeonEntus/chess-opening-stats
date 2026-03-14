import type {
  Platform,
  ColorDashboard,
  AccountInfoResponse,
  HomeView,
} from "@/lib/types"
import { apiClient } from "@/lib/api/apiClient"

export interface DashboardResponse {
  white: ColorDashboard
  black: ColorDashboard
}

/**
 * 백엔드 응답에서 실제 데이터를 추출하는 헬퍼 함수입니다.
 */
function extractData<T>(response: any): T {
  if (response && typeof response === 'object' && 'data' in response && response.data !== null) {
    return response.data as T;
  }
  return response as T;
}

export interface PlayerInfo {
  exists?: boolean
  username?: string
  image_url?: string
  last_online?: number // Unix Timestamp (ms)
}

export interface AddPlayerRequest {
  platform: Platform
  username: string
}

/**
 * 플레이어 존재 여부 확인 및 정보 조회
 * GET /player?platform=...&username=...
 */
export async function checkPlayerExists(platform: string, username: string): Promise<PlayerInfo> {
  const res = await apiClient.get<PlayerInfo>(`/player`, {
    params: { platform, username }
  }).then(res => extractData<PlayerInfo>(res))
  
  // If we got a valid response object with data (like image_url or username),
  // consider the player as existing even if the 'exists' field is missing or false.
  if (res && (res.exists || res.image_url || res.username || res.last_online !== undefined)) {
    return { ...res, exists: true, username: res.username || username }
  }
  
  return { ...res, exists: false }
}

/**
 * 게임 동기화 요청
 * POST /sync?platform=...&username=...
 */
export function syncGames(platform: string, username: string): Promise<{ message: string }> {
  return apiClient.post<{ message: string }>(`/sync`, null, {
    params: { platform, username }
  }).then(res => extractData<{ message: string }>(res))
}

/**
 * 대시보드 데이터 조회
 * GET /dashboard?platform=...&username=...
 */
export function fetchDashboard(platform: string, username: string): Promise<DashboardResponse | null> {
  return apiClient.get<DashboardResponse | null>(`/dashboard`, {
    params: { platform, username }
  }).then(res => extractData<DashboardResponse | null>(res))
}

/**
 * 어카운트 정보 가져오기
 */
export function fetchAccountInfo(): Promise<AccountInfoResponse> {
  return apiClient.get<AccountInfoResponse>("/account").then(res => extractData<AccountInfoResponse>(res))
}

/**
 * 어카운트 전체 동기화
 */
export function syncAccount(): Promise<void> {
  return apiClient.post<void>("/account/sync", {}).then(res => extractData<void>(res))
}

/**
 * 플레이어 연결 해제
 */
export function deletePlayer(playerId: number): Promise<void> {
  return apiClient.delete<void>(`/player/${playerId}`).then(res => extractData<void>(res))
}

/**
 * 플레이어 추가
 */
export function addPlayer(data: AddPlayerRequest): Promise<void> {
  return apiClient.post<void>("/player", data).then(res => extractData<void>(res))
}

/**
 * 홈 뷰 데이터 조회
 */
export function fetchHomeView(): Promise<HomeView> {
  return apiClient.get<HomeView>("/home").then(res => extractData<HomeView>(res))
}

/**
 * 헬스 체크 (필요 시 유지)
 */
export function healthCheck(): Promise<void> {
  return apiClient.get<void>("/health")
}
