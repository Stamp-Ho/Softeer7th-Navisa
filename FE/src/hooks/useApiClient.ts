import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContextProvider";

const BASEURL = "https://api.navisa.site";

let refreshPromise: Promise<any> | null;
const useApiClient = () => {
  const navigate = useNavigate();
  const { accessToken, setAccessToken } = useAuth();
  const apiClient: apiClientType = async <T = any>(
    url: string,
    options: FetchOptions,
  ): Promise<T> => {
    const headers = new Headers(options.headers);
    const currentToken = options.manualToken || accessToken;
    if (!options.skipAuth && currentToken) {
      headers.set("Authorization", `Bearer ${currentToken}`);
    }
    return fetch(`${BASEURL}${url}`, {
      ...defaultOptions,
      ...options,
      headers,
    })
      .then(async (res) => {
        if (!res.ok) {
          if (res.status === 401 && !options.skipAuth) {
            // 이미 재시도를 한 요청인데 또 401이라면 중단 (무한루프 방지)
            if (options._retry)
              throw new Error("Unauthorized even after retry");

            if (!refreshPromise) {
              refreshPromise = refreshAccessToken();
            }
            const newToken = await refreshPromise; // 새 토큰 수령
            refreshPromise = null;
            return apiClient<T>(url, {
              ...options,
              _retry: true,
              manualToken: newToken,
            }); // 재귀 호출 시에도 타입 유지
          }
          throw new Error(String(res.status));
        }
        return res.json() as Promise<T>; // 응답을 T 타입으로 캐스팅
      })
      .catch((error) => {
        console.error("Api 요청 에러 발생: ", error);
        throw new Error("API 호출 에러: " + error);
      });
  };
  // 각 메서드 주입 시 제네릭 적용
  apiClient.get = (
    url: string,
    params?: Record<string, any>,
    options?: FetchOptions,
  ) => {
    const queryString = params
      ? "?" +
        new URLSearchParams(
          Object.entries(params).map(([key, value]) => [
            key,
            Array.isArray(value) ? JSON.stringify(value) : String(value),
          ]),
        ).toString()
      : "";

    return apiClient(`${url}${queryString}`, { ...options, method: "GET" });
  };
  apiClient.post = <T = any>(
    url: string,
    body?: any,
    options?: FetchOptions,
  ): Promise<T> =>
    apiClient<T>(url, {
      ...options,
      method: "POST",
      body: body ? JSON.stringify(body) : undefined,
      headers: { "Content-Type": "application/json", ...options?.headers },
    });

  apiClient.delete = <T = any>(
    url: string,
    options?: FetchOptions,
  ): Promise<T> => apiClient<T>(url, { ...options, method: "DELETE" });

  apiClient.put = <T = any>(url: string, options?: FetchOptions): Promise<T> =>
    apiClient<T>(url, { ...options, method: "PUT" });

  apiClient.patch = <T = any>(
    url: string,
    options?: FetchOptions,
  ): Promise<T> => apiClient<T>(url, { ...options, method: "PATCH" });

  const refreshAccessToken = useCallback(async () => {
    try {
      const res = await fetch(`${BASEURL}/api/auth/reissue`, {
        method: "POST",
        credentials: "include",
      });

      if (res.ok) {
        // 1. JSON 파싱을 먼저 기다립니다.
        const data = await res.json();

        // 2. 이제 실제 데이터를 로그로 확인할 수 있습니다.
        setAccessToken(data.result.accessToken);

        // 3. 발급받은 새로운 액세스 토큰을 반환합니다.
        return data.result.accessToken;
      }

      if (res.status === 401) {
        alert("로그인 시간이 만료되었습니다. 다시 로그인해주세요.");
        navigate("/", { replace: false });
        throw new Error("refresh token 시간 만료");
      }

      throw new Error(`서버 에러: ${res.status}`);
    } catch (err) {
      console.error("Reissue 실패:", err);
      throw err;
    }
  }, [navigate]);

  return { apiClient: apiClient as apiClientType };
};
export default useApiClient;

export type apiClientType = {
  <T = any>(url: string, options: FetchOptions): Promise<T>;
  get<T = any>(
    url: string,
    params?: Record<string, any>,
    options?: FetchOptions,
  ): Promise<T>;
  post<T = any>(url: string, body?: any, options?: FetchOptions): Promise<T>;
  delete<T = any>(url: string, options?: FetchOptions): Promise<T>;
  put<T = any>(url: string, options?: FetchOptions): Promise<T>;
  patch<T = any>(url: string, options?: FetchOptions): Promise<T>;
};

interface FetchOptions extends RequestInit {
  skipAuth?: boolean; // 기본값은 false (즉, 기본적으로 토큰 포함)
  _retry?: boolean;
  manualToken?: string;
}

const defaultOptions: FetchOptions = {
  method: "GET",
  headers: { accept: "*/*" },
};
