import type { OpeningResult, SummaryResponse, OpeningStatsResponse, Platform, AccountInfoResponse } from "@/lib/types"
import { apiClient } from "@/lib/api/apiClient"
import { changePassword as authChangePassword, deleteAccount as authDeleteAccount } from "@/lib/api/auth"
import type { ChangePasswordRequest } from "@/lib/api/auth"

export interface AddPlayerRequest {
  username: string
  platform: Platform
}

export interface DeletePlayerRequest {
  username: string
  platform: Platform
}

export { type ChangePasswordRequest }

export function changePassword(request: ChangePasswordRequest): Promise<void> {
  return authChangePassword(request)
}

export function deleteAccount(): Promise<void> {
  return authDeleteAccount()
}

export function fetchAllOpeningResult(): Promise<OpeningResult[]> {
  return apiClient.get<OpeningStatsResponse>("/stat/account").then(response => response.openingStats)
}

export function fetchSummary(): Promise<SummaryResponse> {
  return apiClient.get<SummaryResponse>("/stat/account/summary")
}

export function syncAccount(): Promise<void> {
  return apiClient.post<void>("/sync")
}

export function addPlayer(request: AddPlayerRequest): Promise<string> {
  return apiClient.post<string>("/player/add", request)
}

export function deletePlayer(request: DeletePlayerRequest): Promise<void> {
  return apiClient.post<void>("/player/delete", request)
}

export function fetchAccountInfo(): Promise<AccountInfoResponse> {
  return apiClient.get<AccountInfoResponse>("/account/info")
}

export function logout(): Promise<void> {
  return apiClient.post<void>("/auth/logout")
}
