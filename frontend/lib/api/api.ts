import type {
  Platform,
  AccountInfoResponse,
  HomeView,
  ApiResponseDto,
} from "@/lib/types"
import { apiClient } from "@/lib/api/apiClient"

export interface AddPlayerRequest {
  username: string
  platform: Platform
}

export interface DeletePlayerRequest {
  username: string
  platform: Platform
}

export function fetchHomeView(): Promise<HomeView> {
  // Uses the new Swagger /views/home endpoint
  return apiClient.get<ApiResponseDto<HomeView>>("/views/home").then((response) => response.data)
}

// /accounts/me/sync (POST)
export function syncAccount(): Promise<void> {
  return apiClient.post<void>("/accounts/me/sync")
}

// /accounts/me/players (POST)
export function addPlayer(request: AddPlayerRequest): Promise<string> {
  return apiClient.post<string>("/accounts/me/players", request)
}

// /accounts/me/players (DELETE)
export function deletePlayer(request: DeletePlayerRequest): Promise<void> {
  return apiClient.delete<void>("/accounts/me/players", { data: request })
}

// /accounts/me (GET)
// fallback to `/views/home` if me is not fully mapping or use exact endpoint
export function fetchAccountInfo(): Promise<AccountInfoResponse> {
  return apiClient.get<ApiResponseDto<AccountInfoResponse>>("/accounts/me").then((response) => response.data)
}

