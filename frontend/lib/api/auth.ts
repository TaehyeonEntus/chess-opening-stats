import { apiClient } from "./apiClient";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  nickname: string;
  password: string;
  passwordConfirm: string;
}

export function login(data: LoginRequest): Promise<void> {
  return apiClient.post<void>("/auth/login", data);
}

export function register(data: RegisterRequest): Promise<void> {
  return apiClient.post<void>("/auth/register", data);
}

export function logout(): Promise<void> {
  return apiClient.post<void>("/auth/logout", {});
}

export function changePassword(data: any): Promise<void> {
    return apiClient.post<void>("/auth/change-password", data);
}
