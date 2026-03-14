import axios, { AxiosRequestConfig } from "axios";

// 1. 기본 설정 (Base Instance)
const axiosInstance = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// 2. 공통 요청 함수 (제네릭 활용)
async function request<T>(config: AxiosRequestConfig): Promise<T> {
    try {
        const response = await axiosInstance.request<T>(config);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;
            const data = error.response?.data;
            const message = data?.message || error.message;
            const code = data?.code || "UNKNOWN_ERROR";
            
            console.error(`[API Error] ${status} (${code}): ${message}`, data);
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

    delete: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
        request<T>({ ...config, method: "DELETE", url, data }),
    };
