import type {
  Platform,
  AccountInfoResponse,
  HomeView,
  ApiResponseDto,
  SyncGameStatus,
} from "@/lib/types"
import { apiClient } from "@/lib/api/apiClient"

export interface AddPlayerRequest {
  username: string
  platform: Platform
}

/**
 * 백엔드 응답에서 실제 데이터를 추출하는 헬퍼 함수입니다.
 * ApiResponseDto { data: ... } 형태이거나 순수 객체인 경우 모두 대응합니다.
 */
function extractData<T>(response: any): T {
  if (response && typeof response === 'object' && 'data' in response && response.data !== null) {
    return response.data as T;
  }
  return response as T;
}

export function fetchHomeView(): Promise<HomeView> {
  return apiClient.get<any>("/views/home").then(res => extractData<HomeView>(res))
}

// /accounts/me/sync (POST)
export function syncAccount(): Promise<void> {
  return apiClient.post<void>("/accounts/me/sync")
}

// /accounts/me/players (POST)
export function addPlayer(request: AddPlayerRequest): Promise<string> {
  return apiClient.post<string>("/accounts/me/players", request)
}

// /accounts/me/players/{playerId} (DELETE)
export function deletePlayer(playerId: number): Promise<void> {
  return apiClient.delete<void>(`/accounts/me/players/${playerId}`)
}

// /health (GET) - CSRF token & session handshake
export function healthCheck(): Promise<void> {
  return apiClient.get<void>("/health")
}

// /accounts/me/sync/status (GET)
export function fetchSyncStatus(): Promise<SyncGameStatus> {
  return apiClient.get<any>("/accounts/me/sync/status").then(res => extractData<SyncGameStatus>(res))
}

// /accounts/me (GET)
export function fetchAccountInfo(): Promise<AccountInfoResponse> {
  return apiClient.get<any>("/accounts/me").then(res => extractData<AccountInfoResponse>(res))
}
