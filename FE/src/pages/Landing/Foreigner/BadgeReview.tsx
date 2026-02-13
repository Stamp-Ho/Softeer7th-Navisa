import { useState } from "react";
import BadgeChip from "./BadgeChip";
import BadgeReviewCard from "./BadgeReviewCard";
import { useAgentBadgeReviewQuery } from "../../../api/hooks/useAgentBadgeReviewQuery";

const BadgeReview = () => {
  const [selectedBadge, setSelectedBadge] = useState<number>(0);
  const { data, isLoading, isError } = useAgentBadgeReviewQuery(selectedBadge);

  const dataToRender =
    isError || isLoading
      ? Array.from({ length: 4 }).map((_, idx) => <BadgeReviewCard key={idx} />)
      : data?.map((review, idx) => (
          <BadgeReviewCard key={idx} review={review} />
        ));

  return (
    <div className="flex flex-col mt-17">
      <h2 className="headline-s-bold">뱃지별 행정사 추천</h2>
      <div className="overflow-x-auto scrollbar-hide mt-6 ">
        <div className="flex flex-row gap-4 w-max">
          {Array.from({ length: 15 }).map((_, idx) => (
            <div onClick={() => setSelectedBadge(idx)}>
              <BadgeChip
                key={idx}
                badgeId={idx}
                isActive={selectedBadge === idx}
              />
            </div>
          ))}
        </div>
      </div>
      <div className="overflow-x-auto scrollbar-hide mt-4 ">
        <div className="flex flex-row gap-4 w-max">{dataToRender}</div>
      </div>
    </div>
  );
};

export default BadgeReview;
