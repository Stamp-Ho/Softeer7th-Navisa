import React, {
	createContext,
	useContext,
	useEffect,
	useState,
	useRef,
	useCallback,
} from 'react';
import {
	connectWebSocket,
	disconnectWebSocket,
	sendMessageToServer,
	isStompConnected,
} from '../api/websocket/stompClient';
import type { Message, Send } from '../api/websocket/types';
import { useAuth } from './AuthContextProvider';
import { refreshPromise } from '../hooks/useApiClient';
import { useQueryClient } from '@tanstack/react-query';
import { DEMO_MODE } from '../config/demoMode';

type WebSocketContextType = {
	messages: Message[];
	sendMessage: (payload: Send) => void;
	isConnected: boolean;
	registerParticipantsInfoCallback: (
		roomId: number,
		callback: () => void,
	) => void;
	unregisterParticipantsInfoCallback: (roomId: number) => void;
};

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider = ({
	children,
}: {
	children: React.ReactNode;
}) => {
	const [messages, setMessages] = useState<Message[]>([]);
	const { accessToken, userId, userType } = useAuth();
	const queryClient = useQueryClient();

	const [isConnected, setIsConnected] = useState(false);
	const isConnectedRef = useRef(false);
	const userIdRef = useRef(userId);
	const participantsInfoCallbacksRef = useRef(new Map<number, () => void>());
	const disconnectPromiseRef = useRef<Promise<void> | null>(null);

	// userId 최신값 유지
	useEffect(() => {
		userIdRef.current = userId;
	}, [userId]);

	useEffect(() => {
		if (DEMO_MODE) {
			setIsConnected(true);
			isConnectedRef.current = true;
			return () => {
				setIsConnected(false);
				isConnectedRef.current = false;
			};
		}

		// 토큰이 없으면 연결 시도 X
		if (!accessToken) return;

		// 권한이 없는 사용자라면 연결 시도 X
		if (
			userType !== 'FILLED_FOREIGNER' &&
			userType !== 'VALID_AGENT' &&
			userType !== 'ADMIN'
		)
			return;

		// 현재 토큰 재발급이 진행 중이라면 완료될 때까지 await으로 기다림
		let cancelled = false;

		const connectWebSocketWithSync = async () => {
			let tokenToUse = accessToken;

			if (refreshPromise) {
				console.log(
					'토큰 재발급이 진행 중입니다. 완료 후 웹소켓을 연결합니다.',
				);
				try {
					const newToken = await refreshPromise;
					// 언마운트 후라면 연결하지 않음
					if (cancelled) return;
					tokenToUse = newToken || accessToken;
				} catch (error) {
					// 토큰 재발급 실패 (세션 만료 등) — 연결 중단
					console.warn('토큰 재발급 실패로 웹소켓 연결을 취소합니다.', error);
					return;
				}
			}

			// 언마운트 후라면 연결하지 않음
			if (cancelled) return;

			// 연결 시작
			connectWebSocket(
				tokenToUse,
				(msg: Message) => {
					// 먼저 사이드 이펙트 처리 (상태 업데이터 외부)
					if (
						msg.type !== 'READ' &&
						msg.type !== 'REVIEW_REQUIRED' &&
						msg.senderId !== userIdRef.current
					) {
						queryClient.refetchQueries({ queryKey: ['chatUnreadCount'] });
						queryClient.refetchQueries({
							queryKey: ['chatMatchedUnreadCount'],
						});
						queryClient.refetchQueries({ queryKey: ['chatRooms'] });
					}

					// ParticipantsInfo 동기화: 상태 업데이터 외부에서 콜백 실행
					if (
						msg.type === 'REVIEW_REQUIRED' ||
						msg.type === 'FEEDBACK_REQUIRED'
					) {
						const callback = participantsInfoCallbacksRef.current.get(
							msg.roomId,
						);
						callback?.();
					}

					setMessages((prev) => {
						if (msg.type === 'READ') {
							return prev.map((m) => {
								if (
									userIdRef.current &&
									m.roomId === msg.roomId &&
									m.senderId === userIdRef.current
								) {
									return { ...m, isRead: true };
								}
								return m;
							});
						} else if (msg.type === 'REVIEW_REQUIRED') {
							return prev;
						} else if (msg.type === 'FEEDBACK_REQUIRED') {
							return [...prev, { ...msg, isRead: false }];
						}
						return [...prev, { ...msg, isRead: false }];
					});
				},
				(connected: boolean) => {
					// 상태 업데이트
					setIsConnected(connected);
					isConnectedRef.current = connected;
				},
			);
		};

		// 이전 연결을 완전히 종료한 후 새로 연결 시작 (좀비 커넥션 방지)
		const setupConnection = async () => {
			// 이전 disconnect가 완료된 후에 새 disconnect를 체이닝
			const prevPromise = disconnectPromiseRef.current ?? Promise.resolve();
			const current = prevPromise.then(() => disconnectWebSocket());
			disconnectPromiseRef.current = current;
			await current;

			if (!cancelled) {
				await connectWebSocketWithSync();
			}
		};

		setupConnection();

		// Cleanup: 컴포넌트 언마운트 시 연결 해제
		return () => {
			cancelled = true;
			const prevPromise = disconnectPromiseRef.current ?? Promise.resolve();
			disconnectPromiseRef.current = prevPromise.then(() =>
				disconnectWebSocket(),
			);
			setIsConnected(false);
			isConnectedRef.current = false;
		};
	}, [accessToken, userType, queryClient]); // accessToken, userType, queryClient이 변경될 때만 재실행

	const handleSendMessage = useCallback((payload: Send) => {
		if (DEMO_MODE) {
			console.info('[DEMO] Skip websocket send', payload.type, payload.roomId);
			return;
		}

		// 1. Context 상태 확인
		if (!isConnectedRef.current) {
			console.warn('Context says disconnected. Cannot send message.');
			return;
		}

		// 2. 실제 클라이언트 연결 상태 확인
		if (!isStompConnected()) {
			console.warn(
				'Actual STOMP client is disconnected. Attempting to reconnect or waiting...',
			);
			return;
		}

		sendMessageToServer(payload);
	}, []);

	const registerParticipantsInfoCallback = useCallback(
		(roomId: number, callback: () => void) => {
			participantsInfoCallbacksRef.current.set(roomId, callback);
		},
		[],
	);

	const unregisterParticipantsInfoCallback = useCallback((roomId: number) => {
		participantsInfoCallbacksRef.current.delete(roomId);
	}, []);

	return (
		<WebSocketContext.Provider
			value={{
				messages,
				sendMessage: handleSendMessage,
				isConnected,
				registerParticipantsInfoCallback,
				unregisterParticipantsInfoCallback,
			}}
		>
			{children}
		</WebSocketContext.Provider>
	);
};

export const useWebSocket = () => {
	const context = useContext(WebSocketContext);
	if (!context) {
		throw new Error('useWebSocket must be used within WebSocketProvider');
	}
	return context;
};
