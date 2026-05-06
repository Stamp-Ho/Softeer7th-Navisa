import { useContext, useEffect, useState } from 'react';
import { AuthContext, type feUserType } from './AuthContext';
import { useLocation, useNavigate } from 'react-router-dom';
import useApiClient from '../hooks/useApiClient';
import { alertT } from '../i18n/alerts';

export const AuthContextProvider = ({
	children,
}: {
	children: React.ReactNode;
}) => {
	const { refreshAccessToken } = useApiClient();
	const navigate = useNavigate();
	const [initialLized, setInitialized] = useState(false);
	const location = useLocation();
	const [userType, setUserType] = useState<feUserType>(() => {
		const savedUserType = localStorage.getItem('userType');
		return savedUserType ? JSON.parse(savedUserType) : null;
	});
	const [userId, setUserId] = useState<string>(() => {
		const savedUserId = localStorage.getItem('userId');
		return savedUserId ? JSON.parse(savedUserId) : null;
	});
	const [accessToken, setAccessToken] = useState<string>('');
	const [isLoggedOut, setIsLoggedOut] = useState(false);

	const getAccessToken = () => {
		return accessToken;
	};
	const logOut = () => {
		setIsLoggedOut(true);
		navigate('/');
		setAccessToken('');
		setUserId('');
		setUserType('NOT_AUTHED');
		localStorage.clear();
	};

	useEffect(() => {
		if (
			location.pathname !== '/' &&
			(userType === 'NOT_AUTHED' || !userType) &&
			!isLoggedOut
		) {
			alertT('pages.landing.loginRequired');
			navigate('/', { replace: true });
		}
		setIsLoggedOut(false);
	}, [location.pathname, userType]);

	// 2. 유저 상태가 바뀔 때마다 로컬 스토리지 업데이트
	useEffect(() => {
		if (userType) {
			localStorage.setItem('userType', JSON.stringify(userType));
		} else {
			localStorage.removeItem('userType');
		}
	}, [userType]);
	useEffect(() => {
		if (userId) {
			localStorage.setItem('userId', JSON.stringify(userId));
		} else {
			localStorage.removeItem('userId');
		}
	}, [userId]);

	useEffect(() => {
		const initAuth = async () => {
			try {
				// Reissue API를 호출해서 메모리에 토큰을 다시 채움
				const newToken = await refreshAccessToken();
				setAccessToken(newToken);
			} catch (e) {
				console.error('로그인 필요 상태');
			} finally {
				// 서버가 내려가 있어도 초기 렌더는 진행되어야 함
				setInitialized(true);
			}
		};
		if (userType && userType !== 'NOT_AUTHED') initAuth();
		else setInitialized(true);
	}, []);
	if (!initialLized) return <></>;
	return (
		<AuthContext.Provider
			value={{
				userType,
				setUserType,
				accessToken,
				setAccessToken,
				getAccessToken,
				userId,
				setUserId,
				logOut,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};

export const useAuth = () => {
	return useContext(AuthContext);
};
