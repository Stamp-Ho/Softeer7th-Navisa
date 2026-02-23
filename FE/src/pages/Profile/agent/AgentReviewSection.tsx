import { useTranslation } from "react-i18next";
import ReviewBar from "./ReviewBar";

const AgentReviewSection = ({
  reviewSummary,
}: {
  reviewSummary?: {
    totalCount: number;
    strengths: { badgeId: number; badgeCount: number }[];
  };
}) => {
  const { t } = useTranslation(["pages"]);
  if (!reviewSummary) return <SkeletonUi />;
  return (
    <>
      <div className="mb-13 headline-m-semibold text-gray-1000">
        {t("profile.totalReviews")} <span className="headline-m-bold text-violet-500">{reviewSummary.totalCount}</span>{" "}
        {t("profile.clientReviewCount")}
      </div>
      <div className="grid grid-cols-2 grid-rows-3 gap-4">
        {reviewSummary.strengths.map((item) => (
          <ReviewBar
            key={item.badgeId}
            badgeId={item.badgeId}
            reviewCount={item.badgeCount}
            totalReviews={reviewSummary.totalCount}
          />
        ))}
        {Array.from({ length: 6 - reviewSummary.strengths.length }).map((_, index) => (
          <div
            key={index}
            className="relative flex flex-row justify-between items-center w-124 h-15 overflow-hidden border border-violet-50 rounded-[12px] bg-background-default"
          />
        ))}
      </div>
    </>
  );
};

export default AgentReviewSection;

const SkeletonUi = () => {
  return (
    <>
      <div className="mb-13 headline-m-semibold text-gray-1000">평가</div>
      <div className="grid grid-cols-2 grid-rows-3 gap-4">
        {Array.from({ length: 6 }).map((_, index) => (
          <div
            key={index}
            className="relative flex flex-row justify-between items-center w-124 h-15 overflow-hidden border border-violet-50 rounded-[12px] bg-background-default"
          />
        ))}
      </div>
    </>
  );
};
