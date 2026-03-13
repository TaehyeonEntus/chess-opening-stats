import axios, { AxiosRequestConfig } from "axios";

// 1. 기본 설정 (Base Instance)
const axiosInstance = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

// Helper function to get cookie by name
function getCookie(name: string): string | null {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(";").shift() || null;
    return null;
}

// Helper function to clear session and redirect to login
function handleAuthError() {
    if (typeof document !== "undefined") {
        // 클라이언트 측에서 지울 수 있는 쿠키 시도 (XSRF 등)
        const cookieOptions = `; path=/; domain=${process.env.NEXT_PUBLIC_COOKIE_DOMAIN || ''}; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
        document.cookie = `XSRF-TOKEN=${cookieOptions}`;
        document.cookie = `JSESSIONID=${cookieOptions}`;
        
        // 서버 측 쿠키 삭제 API를 호출하여 HttpOnly 쿠키 강제 제거 후 로그인 이동
        if (!window.location.pathname.includes("/login")) {
            window.location.href = "/api/auth/clear";
        }
    }
}

// 2. Request Interceptor for Manual CSRF Handling
axiosInstance.interceptors.request.use((config) => {
    if (typeof document !== "undefined" && config.method !== "get" && config.method !== "head") {
        const token = getCookie("XSRF-TOKEN");
        if (token) {
            config.headers["X-XSRF-TOKEN"] = token;
        }
    }
    return config;
});

// 3. Response Interceptor for Global Error Handling (401, Session Timeout)
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;
            const message = error.response?.data?.message;

            // 401 Unauthorized 또는 특정 인증 오류 메시지 감지 시 세션 정리
            if (status === 401 || message === "Authentication required") {
                console.warn("[Auth] Session expired or invalid. Clearing security context.");
                handleAuthError();
            }
        }
        return Promise.reject(error);
    }
);

// 4. 공통 요청 함수 (제네릭 활용)
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
            
            // 401 에러는 인터셉터에서 처리하므로 여기서는 로깅만 가볍게 수행하거나 중복 로깅 방지
            if (status !== 401) {
                console.error(`[API Error] ${status} (${code}): ${message}`, data);
            }
        }
        throw error;
    }
}

// 5. apiClient 객체 노출
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