import { useRef, useState, useEffect } from "react";
import AgentCard from "../../../components/domain/AgentCard";
import LoadingBar from "../Common/LoadingBar";
import { useRecommendedAgentQuery } from "../../../api/queries/useRecommendedAgentQuery";
import { useTranslation } from "react-i18next";

const SuggestedAgents = () => {
  const { t } = useTranslation(["pages"]);
  const { data, isLoading, isError } = useRecommendedAgentQuery();
  const [loading, setLoading] = useState<boolean>(true);
  const [showLoadingBar, setShowLoadingBar] = useState<boolean>(true);
  const [isTilted, setIsTilted] = useState<boolean>(true);
  const flyTime = 1000;
  const scrollRef = useRef<HTMLDivElement>(null);
  const endRef = useRef<HTMLDivElement>(null); // 마지막 빈 div를 위한 ref

  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);

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

  useEffect(() => {
    const scrollContainer = scrollRef.current;
    if (scrollContainer) {
      const olElement = scrollContainer.childNodes[1] as HTMLElement;
      const onWheel = (event: WheelEvent) => {
        if (!isLoading && !loading && !isError && olElement) {
          // 1. olElement의 실제 콘텐츠 너비가 컨테이너보다 작거나 같으면 스크롤 방지
          if (olElement.scrollWidth <= scrollContainer.clientWidth) {
            return; // 아무것도 하지 않음
          }
          event.preventDefault();
          if (Math.abs(event.deltaX) > Math.abs(event.deltaY)) scrollContainer.scrollLeft += event.deltaX;
          else scrollContainer.scrollLeft += event.deltaY;
        }
      };

      scrollContainer.addEventListener("wheel", onWheel);
      return () => {
        scrollContainer.removeEventListener("wheel", onWheel);
      };
    }
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

  useEffect(() => {
    const startLoading = () => {
      setLoading(true);
      setShowLoadingBar(true);
      setIsTilted(true);
    };

    const finishLoading = async () => {
      const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));
      // 1. 로딩 바 숨기기
      setShowLoadingBar(false);

      // 2. 750ms 대기 후 로딩 상태 해제
      await delay(750);
      setLoading(false);

      // 3. flyTime만큼 대기 후 기울기 복구
      await delay(flyTime);
      setIsTilted(false);
    };

    startLoading();
    if (!isLoading) {
      setTimeout(() => finishLoading(), 2000);
    }
  }, [isLoading]);

  const dataToRender = isError ? (
    <>
      {Array.from({ length: 12 }).map((_, idx) => (
        <AgentCard key={idx} className={`${isTilted ? "-rotate-10 -mr-4" : "rotate-0"} duration-500 transition-all `} />
      ))}
    </>
  ) : (
    <>
      {data?.map((agent, idx) => (
        <AgentCard
          key={idx}
          agent={agent}
          className={`${isTilted ? "-rotate-10 -mr-4" : "rotate-0"} duration-500 transition-all `}
        />
      ))}
    </>
  );
  return (
    <section className="w-full flex flex-col relative">
      <h2 className="headline-s-bold">{t("landing.suggestedAgents")}</h2>
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`${getMaskStyle()} ${loading ? "overflow-x-hidden" : "overflow-x-auto"}
        scrollbar-hide px-4 -mx-4 flex flex-row items-center`}
      >
        {loading && <LoadingBar isCompleted={!showLoadingBar} />}
        <ol
          className={`${loading ? "pl-500" : ""}
            transition-all duration-1000
            flex flex-row gap-5 w-fit items-center justify-start h-104`}
        >
          {dataToRender}
          {/* 4. 마지막 감지용 빈 div */}
          <div ref={endRef} className="w-3 h-1 -ml-6" />
        </ol>
      </div>
    </section>
  );
};

export default SuggestedAgents;
