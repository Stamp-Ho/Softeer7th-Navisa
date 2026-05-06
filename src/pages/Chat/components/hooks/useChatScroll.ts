import { useRef, useState, useLayoutEffect } from "react";
import type { ChatHistoryResponse } from "../../../../api/types/chat";

interface UseChatScrollProps {
  chatData: ChatHistoryResponse[];
  hasNextPage?: boolean;
  isFetchingNextPage?: boolean;
  fetchNextPage?: () => void;
}

export const useChatScroll = ({
  chatData,
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
}: UseChatScrollProps) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const [isAtBottom, setIsAtBottom] = useState(true);
  const prevScrollHeightRef = useRef<number>(0);

  const handleScroll = () => {
    if (!scrollRef.current) return;

    const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;
    const bottomHeight = scrollHeight - scrollTop - clientHeight;

    setIsAtBottom(bottomHeight <= 50);

    if (
      scrollTop <= 700 &&
      hasNextPage &&
      !isFetchingNextPage &&
      fetchNextPage
    ) {
      // 데이터 요청 직전의 높이 저장
      prevScrollHeightRef.current = scrollHeight;
      fetchNextPage();
    }
  };

  useLayoutEffect(() => {
    if (!scrollRef.current || prevScrollHeightRef.current === 0) return;

    const currentScrollHeight = scrollRef.current.scrollHeight;
    const prevHeight = prevScrollHeightRef.current;

    // 높이가 이전보다 커졌을 때만 로직 수행
    if (currentScrollHeight > prevHeight) {
      const diff = currentScrollHeight - prevHeight;

      // 스크롤 보정
      scrollRef.current.scrollTop = scrollRef.current.scrollTop + diff;

      // 성공적으로 보정했을 때만 0으로 초기화
      prevScrollHeightRef.current = 0;
    }
  }, [chatData]);

  const scrollToBottom = () => {
    if (!scrollRef.current) return;
    scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
  };

  return {
    scrollRef,
    handleScroll,
    scrollToBottom,
    isAtBottom,
  };
};
