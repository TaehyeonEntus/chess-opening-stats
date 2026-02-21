import axios, { AxiosRequestConfig } from "axios";

// 1. 기본 설정 (Base Instance)
const axiosInstance = axios.create({
    baseURL: process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

// 2. 공통 요청 함수 (제네릭 활용)
async function request<T>(config: AxiosRequestConfig): Promise<T> {
    try {
        const response = await axiosInstance.request<T>(config);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            // 에러 로깅이나 커스텀 에러 처리를 여기서 수행합니다.
            const status = error.response?.status;
            const message = error.response?.data?.message || error.message;
            console.error(`[API Error] ${status}: ${message}`);
        }
        throw error;
    }
}

// 3. apiClient 객체 노출
export const apiClient = {
    get: <T>(url: string, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "GET", url }),

    post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "POST", url, data }),

    put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "PUT", url, data }),

    patch: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "PATCH", url, data }),

    delete: <T>(url: string, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "DELETE", url }),
};