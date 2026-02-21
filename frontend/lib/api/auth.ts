import { apiClient } from "@/lib/api/apiClient"

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

export function login(request: LoginRequest): Promise<void> {
  return apiClient.post<void>("/api/auth/login", request)
}

export function logout(): Promise<void> {
  return apiClient.post<void>("/api/auth/logout")
}

export function register(request: RegisterRequest): Promise<string> {
  return apiClient.post<string>("/api/auth/register", request)
}

export function changePassword(request: ChangePasswordRequest): Promise<void> {
  return apiClient.post<void>("/api/auth/changePassword", request)
}

export function deleteAccount(): Promise<void> {
  return apiClient.post<void>("/api/auth/delete")
}
