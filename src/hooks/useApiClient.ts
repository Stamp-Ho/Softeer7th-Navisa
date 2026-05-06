import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContextProvider';
import { DEMO_MODE } from '../config/demoMode';
import { handleDemoApiRequest } from '../api/mock/demoApi';

const BASEURL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.navisa.site';

export let refreshPromise: Promise<string> | null = null;

const useApiClient = () => {
	const navigate = useNavigate();
	const { accessToken, setAccessToken, setUserId, setUserType, userType } =
		useAuth();
	const apiClient: apiClientType = async <T = any>(
		url: string,
		options: FetchOptions,
	): Promise<T> => {
		const headers = new Headers(options.headers);
		const currentToken = options.manualToken || accessToken;
		if (!options.skipAuth && currentToken) {
			headers.set('Authorization', `Bearer ${currentToken}`);
		}

		if (DEMO_MODE) {
			return handleDemoApiRequest<T>(url, {
				...defaultOptions,
				...options,
				headers,
			});
		}

		return fetch(`${BASEURL}${url}`, {
			...defaultOptions,
			...options,
			headers,
		})
			.then(async (res) => {
				if (!res.ok) {
					if (
						res.status === 401 &&
						!options.skipAuth &&
						userType !== 'NOT_AUTHED'
					) {
						// 이미 재시도를 한 요청인데 또 401이라면 중단 (무한루프 방지)
						if (options._retry)
							throw new Error('Unauthorized even after retry');

						const newToken = await refreshAccessToken();
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
				//console.error("Api 요청 에러 발생: ", error);
				throw new Error('API 호출 에러: ' + error);
			});
	};
	// 각 메서드 주입 시 제네릭 적용
	apiClient.get = (
		url: string,
		params?: Record<string, any>,
		options?: FetchOptions,
	) => {
		const getQueryString = (params: Record<string, any>) => {
			if (!params) return '';

			const searchParams = new URLSearchParams();

			Object.entries(params).forEach(([key, value]) => {
				if (Array.isArray(value)) {
					// 배열인 경우 각 요소를 순회하며 동일한 key로 append
					value.forEach((v) => {
						// v가 null이나 undefined가 아닐 때만 추가
						if (v !== null && v !== undefined) {
							searchParams.append(key, String(v));
						}
					});
				} else if (value !== null && value !== undefined) {
					// 배열이 아닌 일반 값 처리
					searchParams.append(key, String(value));
				}
			});

			const baseQuery = searchParams.toString();

			return baseQuery ? `?${baseQuery}` : '';
		};

		const queryString = params ? getQueryString(params) : '';

		return apiClient(`${url}${queryString}`, { ...options, method: 'GET' });
	};
	apiClient.post = <T = any>(
		url: string,
		body?: any,
		options?: FetchOptions,
	): Promise<T> =>
		apiClient<T>(url, {
			...options,
			method: 'POST',
			body: body ? JSON.stringify(body) : undefined,
			headers: { 'Content-Type': 'application/json', ...options?.headers },
		});

	apiClient.delete = <T = any>(
		url: string,
		options?: FetchOptions,
	): Promise<T> => apiClient<T>(url, { ...options, method: 'DELETE' });

	apiClient.put = <T = any>(url: string, options?: FetchOptions): Promise<T> =>
		apiClient<T>(url, { ...options, method: 'PUT' });

	apiClient.patch = <T = any>(
		url: string,
		body?: any,
		options?: FetchOptions,
	): Promise<T> =>
		apiClient<T>(url, {
			...options,
			method: 'PATCH',
			body: body ? JSON.stringify(body) : undefined,
			headers: { 'Content-Type': 'application/json', ...options?.headers },
		});

	const refreshAccessToken = useCallback(async () => {
		if (DEMO_MODE) {
			const demoToken = 'demo-access-token';
			setAccessToken(demoToken);
			return demoToken;
		}

		if (refreshPromise) return refreshPromise;
		refreshPromise = (async () => {
			try {
				const res = await fetch(`${BASEURL}/api/auth/reissue`, {
					method: 'POST',
					credentials: 'include',
				});

				if (res.ok) {
					const data = await res.json();
					const newToken = data.result.accessToken;
					setAccessToken(newToken);
					return newToken;
				}
				// 에러 처리...
				if (res.status === 401) {
					alert('로그인 시간이 만료되었습니다. 다시 로그인해주세요.');
					setUserId('');
					setUserType('NOT_AUTHED');
					setAccessToken('');
					window.localStorage.removeItem('userType');
					window.localStorage.removeItem('userId');
					navigate('/', { replace: false });
					location.reload();
					throw new Error('refresh token 시간 만료');
				}
				if (res.status === 409) {
					alert('다른 기기에서 로그인한 계정입니다. 다시 로그인해주세요.');
					setUserId('');
					setUserType('NOT_AUTHED');
					setAccessToken('');
					window.localStorage.removeItem('userType');
					window.localStorage.removeItem('userId');
					navigate('/', { replace: false });
					location.reload();
					throw new Error('다른 기기에서 로그인 감지');
				}
			} finally {
				// API 응답 직후 몰려오는 다른 401 요청들이 새로운 reissue를 쏘지 않도록 방어막 형성
				setTimeout(() => {
					refreshPromise = null;
				}, 500);
			}
		})();

		return refreshPromise;
	}, [navigate, setAccessToken, setUserId, setUserType]);

	return { apiClient: apiClient as apiClientType, refreshAccessToken };
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
	patch<T = any>(url: string, body?: any, options?: FetchOptions): Promise<T>;
};

interface FetchOptions extends RequestInit {
	skipAuth?: boolean; // 기본값은 false (즉, 기본적으로 토큰 포함)
	_retry?: boolean;
	manualToken?: string;
}

const defaultOptions: FetchOptions = {
	method: 'GET',
	headers: { accept: '*/*' },
};
