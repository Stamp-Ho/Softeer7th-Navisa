import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import BadgeChip from "./BadgeChip";
import BadgeReviewCard from "./BadgeReviewCard";
import { useAgentBadgeReviewQuery } from "../../../api/queries/useAgentBadgeReviewQuery";

const BadgeReview = () => {
  const { t } = useTranslation(["pages"]);
  const [selectedBadge, setSelectedBadge] = useState<number>(0);
  const { data, isLoading, isError } = useAgentBadgeReviewQuery(selectedBadge + 1);
  const scrollRef = useRef<HTMLDivElement>(null);
  const badgeScrollRef = useRef<HTMLDivElement>(null);

  const [isBadgeAtStart, setIsBadgeAtStart] = useState(true);
  const [isBadgeAtEnd, setIsBadgeAtEnd] = useState(false);
  const [isAtStart, setIsAtStart] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);

  useEffect(() => {
    const scrollContainer = scrollRef.current;
    if (!scrollContainer) return;

    const olElement = scrollContainer.childNodes[0] as HTMLElement;
    if (!olElement) return;

    const onWheel = (event: WheelEvent) => {
      if (olElement.scrollWidth <= scrollContainer.clientWidth) {
        return;
      }
      event.preventDefault();
      if (Math.abs(event.deltaX) > Math.abs(event.deltaY)) {
        scrollContainer.scrollLeft += event.deltaX;
      } else {
        scrollContainer.scrollLeft += event.deltaY;
      }
    };
    scrollContainer.addEventListener("wheel", onWheel, { passive: false });
    return () => {
      scrollContainer.removeEventListener("wheel", onWheel);
    };
  }, []);

  useEffect(() => {
    const badgeScrollContainer = badgeScrollRef.current;
    if (!badgeScrollContainer) return;

    const onWheel = (event: WheelEvent) => {
      if (badgeScrollContainer.scrollWidth <= badgeScrollContainer.clientWidth) {
        return;
      }
      event.preventDefault();
      if (Math.abs(event.deltaX) > Math.abs(event.deltaY)) {
        badgeScrollContainer.scrollLeft += event.deltaX;
      } else {
        badgeScrollContainer.scrollLeft += event.deltaY;
      }
    };
    badgeScrollContainer.addEventListener("wheel", onWheel, { passive: false });
    return () => {
      badgeScrollContainer.removeEventListener("wheel", onWheel);
    };
  }, []);

  const handleBadgeScroll = () => {
    if (badgeScrollRef.current) {
      setIsBadgeAtStart(badgeScrollRef.current.scrollLeft <= 20);
      setIsBadgeAtEnd(
        badgeScrollRef.current.scrollWidth - badgeScrollRef.current.scrollLeft - badgeScrollRef.current.clientWidth <=
          20,
      );
    }
  };

  const handleScroll = () => {
    if (scrollRef.current) {
      setIsAtStart(scrollRef.current.scrollLeft <= 20);
      setIsAtEnd(scrollRef.current.scrollWidth - scrollRef.current.scrollLeft - scrollRef.current.clientWidth <= 20);
    }
  };

  const dataToRender = isLoading ? (
    Array.from({ length: 4 }).map((_, idx) => <BadgeReviewCard key={idx} />)
  ) : isError ? (
    <div className="h-43 p-3 title-s-medium">불러올 리뷰가 없습니다.</div>
  ) : (
    data?.map((review, idx) => <BadgeReviewCard key={idx} review={review} />)
  );
  const getMaskStyle = (isAtStart: boolean, isAtEnd: boolean) => {
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

  return (
    <div className="flex flex-col mt-17">
      <h2 className="headline-s-bold">{t("landing.badgeRecommendation")}</h2>
      <div
        className={`overflow-x-auto scrollbar-hide mt-6 ${getMaskStyle(isBadgeAtStart, isBadgeAtEnd)}`}
        ref={badgeScrollRef}
        onScroll={handleBadgeScroll}
      >
        <div className="flex flex-row gap-4 w-max py-0.5 px-0.5">
          {Array.from({ length: 15 }).map((_, idx) => (
            <button
              type="button"
              className="cursor-pointer"
              onClick={() => setSelectedBadge(idx)}
              key={`badge_${idx}`}
              tabIndex={0}
            >
              <BadgeChip key={idx} badgeId={idx} isActive={selectedBadge === idx} />
            </button>
          ))}
        </div>
      </div>
      <div
        className={`overflow-x-auto scrollbar-hide mt-4 ${getMaskStyle(isAtStart, isAtEnd)}`}
        ref={scrollRef}
        onScroll={handleScroll}
      >
        <div className="flex flex-row gap-4 w-max p-2">{dataToRender}</div>
      </div>
    </div>
  );
};

export default BadgeReview;
