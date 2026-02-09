import { useRef, useState, useEffect } from "react";
import { useRecommendedForeignerQuery } from "../../../api/hooks/useRecommendedForeignerQuery";
import RecommendedForeignerCard from "../../../components/shared/RecommendedForeignerCard";

const SuggestedForeigners = () => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null); // 마지막 빈 div를 위한 ref

  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);
  const { data, isLoading, isError } = useRecommendedForeignerQuery();

  // 1. 스크롤 위치 감지 (왼쪽 끝 체크용)
  const handleScroll = () => {
    if (scrollRef.current) {
      setIsAtStart(scrollRef.current.scrollLeft <= 20);
    }
  };

  // 2. Intersection Observer (오른쪽 끝 체크용)
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        setIsAtEnd(entry.isIntersecting); // 마지막 div가 보이면 true
      },
      { threshold: 0.1 }, // 10%만 보여도 감지
    );

    if (endRef.current) observer.observe(endRef.current);
    return () => observer.disconnect();
  }, []);

  // 3. 상태에 따른 마스크 스타일 결정
  const getMaskStyle = () => {
    const base = "transition-all duration-500 ";
    if (isAtStart)
      return (
        base +
        `mask-[linear-gradient(to_right,black_90%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_right,black_90%,transparent_100%)]`
      );
    if (isAtEnd)
      return (
        base +
        `mask-[linear-gradient(to_left,black_90%,transparent_100%)]
        [-webkit-mask-image:linear-gradient(to_left,black_90%,transparent_100%)]`
      );
    return (
      base +
      `mask-[linear-gradient(to_right,transparent_0%,black_10%,black_90%,transparent_100%)]
      [-webkit-mask-image:linear-gradient(to_right,transparent_0%,black_10%,black_90%,transparent_100%)]`
    );
  };

  if (isLoading) return <div>로딩중...</div>;
  const dataToRender = isError ? (
    <>
      {Array.from({ length: 12 }).map((_, idx) => (
        <RecommendedForeignerCard key={`foreignerCard_${idx}`} />
      ))}{" "}
    </>
  ) : (
    <>
      {data?.map((foreigner, idx) => (
        <RecommendedForeignerCard
          key={`foreignerCard_${idx}`}
          foreigner={foreigner}
        />
      ))}
    </>
  );

  return (
    <section className="w-full flex flex-col relative mt-12.5">
      <h2 className="headline-s-bold">
        행정사님의 도움이 필요한 새로운 의뢰인
      </h2>
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`${getMaskStyle()} overflow-x-auto
        scrollbar-hide px-4 -mx-4 flex flex-row items-center`}
      >
        <ol
          className={`
            flex flex-row gap-5 w-fit items-center justify-start my-6`}
        >
          {dataToRender}
          {/* 4. 마지막 감지용 빈 div */}
          <div ref={endRef} className="w-3 h-1 -ml-6" />
        </ol>
      </div>
    </section>
  );
};

export default SuggestedForeigners;
