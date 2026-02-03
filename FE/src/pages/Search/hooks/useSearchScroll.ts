import { useRef, useState } from "react";

export const useSearchScroll = (onCloseToBottom = () => {}) => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null);

  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);

  const handleScroll = () => {
    if (scrollRef.current) {
      const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;

      // scrollHeight(전체높이) - scrollTop(내려온길이) - clientHeight(보이는높이) = 화면 아래의 높이
      const bottomHeight = scrollHeight - scrollTop - clientHeight;

      setIsAtStart(scrollTop <= 20); // 상단 도달 체크 (여유값 20px)
      setIsAtEnd(bottomHeight <= 20); //스타일 적용

      if (bottomHeight <= 50) onCloseToBottom(); //다음 페이지 가져오기
    }
  };
  const goTop = () => {
    if (scrollRef.current)
      scrollRef.current.scrollTo({ top: 0, behavior: "smooth" });
  };

  const searchResultStyle = () => {
    const maskStyle = {
      atStart: "linear-gradient(to_bottom,black_85%,transparent_100%)",
      atEnd: "linear-gradient(to_top,black_85%,transparent_100%)",
      none: "linear-gradient(to_bottom,transparent_0%,black_15%,black_85%,transparent_100%)",
    };
    let currentStyle = maskStyle["none"];
    if (isAtStart) currentStyle = maskStyle["atStart"];
    if (isAtEnd) currentStyle = maskStyle["atEnd"];
    return `transition-all duration-500  mask-[${currentStyle}] [-webkit-mask-image:${currentStyle}]`;
  };

  return {
    scrollRef,
    handleScroll,
    searchResultStyle,
    endRef,
    goTop,
  };
};
