import { apiClient } from "@/lib/api/apiClient"
import { SyncGameResponse } from "@/lib/types"

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  passwordConfirm: string
  nickname: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
  newPasswordConfirm: string
}

// /login (POST) - Authentication tag
export function login(request: LoginRequest): Promise<void> {
  return apiClient.post<void>("/login", request)
}

// Swagger 스펙에는 명시적인 /logout 엔드포인트가 없으므로,
// 서버가 제공하는 경우를 대비해 기본 경로만 호출하도록 최소한으로 유지한다.
export function logout(): Promise<void> {
  return apiClient.post<void>("/logout")
}

// /accounts/register (POST)
export function register(request: RegisterRequest): Promise<string> {
  return apiClient.post<string>("/accounts/register", request)
}

// /accounts/me/password (PATCH)
export function changePassword(request: ChangePasswordRequest): Promise<void> {
  return apiClient.patch<void>("/accounts/me/password", request)
}

// /accounts/me (DELETE)
export function deleteAccount(): Promise<void> {
  return apiClient.delete<void>("/accounts/me")
}

// /accounts/me/sync (POST)
export function syncGames(): Promise<SyncGameResponse> {
  return apiClient.post<SyncGameResponse>("/accounts/me/sync")
}
