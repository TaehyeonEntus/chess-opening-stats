import { apiClient } from "@/lib/api/apiClient"
import type { 
  PlayerInfo 
} from "@/lib/types"
import { MOCK_DASHBOARD_DATA } from "./mock-data"

/**
 * 플레이어 존재 여부 확인 및 정보 조회
 * GET /player?platform=...&username=...
 */
export async function checkPlayerExists(platform: string, username: string): Promise<PlayerInfo> {
  if (username === "demo") {
    return {
      exists: true,
      username: "demo",
      image_url: "https://github.com/shadcn.png", // Demo avatar
      last_online: 0 // Just now
    }
  }

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
 * 게임 동기화 SSE URL 가져오기
 * GET /sync?platform=...&username=...
 */
export function getSyncEventSourceUrl(platform: string, username: string): string {
  if (username === "demo") {
    return ""; // Special case for demo
  }
  return `/api/sync?platform=${platform}&username=${username}`;
}
