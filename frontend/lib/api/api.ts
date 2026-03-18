import { apiClient } from "@/lib/api/apiClient"
import type { 
  PlayerInfo, 
  SyncGameResponse, 
  DashboardResponse 
} from "@/lib/types"

/**
 * 플레이어 존재 여부 확인 및 정보 조회
 * GET /player?platform=...&username=...
 */
export async function checkPlayerExists(platform: string, username: string): Promise<PlayerInfo> {
  const res = await apiClient.get<any>(`/player`, {
    params: { platform, username }
  });
  
  // 백엔드가 데이터를 직접 반환함 (image_url, last_online)
  // 데이터가 있으면 존재하는 것으로 간주
  if (res && (res.image_url || res.last_online !== undefined)) {
    return {
      exists: true,
      username: username,
      image_url: res.image_url,
      last_online: res.last_online
    }
  }
  
  return { exists: false, username }
}

/**
 * 게임 동기화 요청
 * POST /sync?platform=...&username=...
 */
export function syncGames(platform: string, username: string): Promise<SyncGameResponse> {
  return apiClient.post<SyncGameResponse>(`/sync`, null, {
    params: { platform, username }
  });
}

/**
 * 대시보드 데이터 조회
 * GET /dashboard?platform=...&username=...
 */
export function fetchDashboard(platform: string, username: string): Promise<DashboardResponse | null> {
  return apiClient.get<DashboardResponse | null>(`/dashboard`, {
    params: { platform, username }
  });
}
