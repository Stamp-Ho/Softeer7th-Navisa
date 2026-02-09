import { useCallback } from "react";
import { useNavigate } from "react-router-dom";

const BASEURL = "https://api.navisa.site";

let refreshPromise: Promise<any> | null;
const useApiClient = () => {
  const navigate = useNavigate();
  const apiClient: apiClientType = async <T = any>(
    url: string,
    options: FetchOptions,
  ): Promise<T> => {
    return fetch(`${BASEURL}${url}`, {
      ...defaultOptions,
      ...options,
    })
      .then(async (res) => {
        if (!res.ok) {
          if (res.status === 401) {
            if (!refreshPromise) {
              refreshPromise = refreshAccessToken();
            }
            await refreshPromise;
            refreshPromise = null;
            return apiClient<T>(url, options); // 재귀 호출 시에도 타입 유지
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
    await fetch(`${BASEURL}/api/auth/reissue`, { method: "POST" })
      .then((res) => {
        if (res.ok) return;
        if (res.status === 401) {
          alert("로그인 시간이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/", { replace: false });
          throw new Error("refresh token 시간 만료");
        } else throw new Error("Reissue 실패!");
      })
      .catch((err) => {
        console.error("Reissue 실패: ", err);
        throw new Error("Reissue 에러: " + err);
      });
    return;
  }, []);

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

type FetchOptions = {
  method?: "POST" | "GET" | "PUT" | "PATCH" | "DELETE";
  mode?: "cors" | "no-cors" | "same-origin";
  cache?: "default" | "no-cache" | "reload" | "force-cache" | "only-if-cached";
  credentials?: "same-origin" | "include" | "omit";
  headers?: {};
  redirect?: "follow" | "manual" | "error";
  referrerPolicy?:
    | "no-referrer"
    | "no-referrer-when-downgrade"
    | "origin"
    | "origin-when-cross-origin"
    | "same-origin"
    | "strict-origin"
    | "strict-origin-when-cross-origin"
    | "unsafe-url";
  body?: string;
};

const defaultOptions: FetchOptions = {
  method: "GET",
  headers: { accept: "*/*" },
  credentials: "include",
};
