import { useRef, useState, useCallback } from "react";

export const useOnboardScroll = () => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);
  const [currentSectionIndex, setCurrentSectionIndex] = useState(0);

  // 스크롤 이벤트 핸들러
  const handleScroll = useCallback(() => {
    if (!scrollRef.current) return;

    const container = scrollRef.current;
    const { scrollTop, scrollHeight, clientHeight } = container;

    // 1. 상단/하단 도달 체크
    setIsAtStart(scrollTop <= 20);
    setIsAtEnd(scrollHeight - scrollTop - clientHeight <= 20);

    // 2. 중앙 섹션 감지 (질문 단위)
    const targetContainers = Array.from(
      container.children[0]?.lastChild?.childNodes ?? [],
    ).flatMap((n) => Array.from(n.childNodes).slice(1));
    if (!targetContainers) return;

    const containerRect = container.getBoundingClientRect();
    const viewportCriteria = containerRect.top + 100;

    targetContainers.forEach((question, index) => {
      if (!(question instanceof HTMLElement)) return;

      const rect = question.getBoundingClientRect();
      if (viewportCriteria >= rect.top && viewportCriteria <= rect.bottom) {
        setCurrentSectionIndex(index);
      }
    });
  }, []);

  // 특정 섹션으로 이동
  const goToSection = useCallback((index: number) => {
    if (!scrollRef.current) return;

    const container = scrollRef.current;
    const targetSection = Array.from(
      container.children[0]?.lastChild?.childNodes ?? [],
    ).flatMap((n) => Array.from(n.childNodes).slice(1))[index] as HTMLElement;

    if (!targetSection) return;

    const containerRect = container.getBoundingClientRect();
    const targetRect = targetSection.getBoundingClientRect();
    const scrollTarget =
      targetRect.top - containerRect.top + container.scrollTop;

    container.scrollTo({
      top: scrollTarget - 30,
      behavior: "smooth",
    });
  }, []);

  // 최상단 이동
  const goTop = () => {
    scrollRef.current?.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  // 그라데이션 마스크 스타일 계산
  const getMaskStyle = useCallback(() => {
    const base = "transition-all duration-500 ";
    const maskStart =
      "mask-[linear-gradient(to_bottom,black_97%,transparent_100%)] [-webkit-mask-image:linear-gradient(to_bottom,black_97%,transparent_100%)]";
    const maskEnd =
      "mask-[linear-gradient(to_top,black_97%,transparent_100%)] [-webkit-mask-image:linear-gradient(to_top,black_97%,transparent_100%)]";
    const maskBoth =
      "mask-[linear-gradient(to_bottom,transparent_0%,black_3%,black_97%,transparent_100%)] [-webkit-mask-image:linear-gradient(to_bottom,transparent_0%,black_3%,black_97%,transparent_100%)]";

    let currentMask = maskBoth;
    if (isAtStart) currentMask = maskStart;
    else if (isAtEnd) currentMask = maskEnd;

    return `${base} ${currentMask}`;
  }, [isAtStart, isAtEnd]);

  return {
    scrollRef,
    handleScroll,
    goToSection,
    goTop,
    currentSectionIndex,
    getMaskStyle,
  };
};
