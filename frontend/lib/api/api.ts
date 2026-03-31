import { apiClient } from "@/lib/api/apiClient"
import type { 
  PlayerInfo 
} from "@/lib/types"
import { MOCK_DASHBOARD_DATA } from "./mock-data"

/**
 * 플레이어 존재 여부 확인 및 정보 조회
 * GET /player/profile?platform=...&username=...
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

  try {
    const res = await apiClient.get<any>(`/player/profile`, {
      params: { platform, username }
    });
    
    // 백엔드가 데이터를 직접 반환함 (image_url, last_online)
    if (res && (res.image_url || res.last_online !== undefined)) {
      return {
        exists: true,
        username: username,
        image_url: res.image_url,
        last_online: res.last_online
      }
    }
  } catch (error) {
    console.error("Failed to check player existence", error);
  }
  
  return { exists: false, username }
}

/**
 * 게임 데이터 수집 작업 추가
 * POST /task?platform=...&username=...
 */
export async function addTask(platform: string, username: string): Promise<void> {
  if (username === "demo") return;
  
  await apiClient.post(`/task`, null, {
    params: { platform, username }
  });
}

/**
 * 대시보드 데이터 조회 (폴링용)
 * GET /dashboard?platform=...&username=...
 */
export async function fetchDashboard(platform: string, username: string): Promise<any> {
  if (username === "demo") return MOCK_DASHBOARD_DATA;
  
  return await apiClient.get(`/dashboard`, {
    params: { platform, username }
  });
}
