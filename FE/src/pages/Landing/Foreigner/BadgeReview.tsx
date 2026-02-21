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

  const dataToRender = isLoading ? (
    Array.from({ length: 4 }).map((_, idx) => <BadgeReviewCard key={idx} />)
  ) : isError ? (
    <div className="h-43 p-3 title-s-medium">불러올 리뷰가 없습니다.</div>
  ) : (
    data?.map((review, idx) => <BadgeReviewCard key={idx} review={review} />)
  );

  return (
    <div className="flex flex-col mt-17">
      <h2 className="headline-s-bold">{t("landing.badgeRecommendation")}</h2>
      <div className="overflow-x-auto scrollbar-hide mt-6 ">
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
      <div className="overflow-x-auto scrollbar-hide mt-4 " ref={scrollRef}>
        <div className="flex flex-row gap-4 w-max p-2">{dataToRender}</div>
      </div>
    </div>
  );
};

export default BadgeReview;
