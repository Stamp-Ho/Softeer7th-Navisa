import { useTranslation } from "react-i18next";
import ReviewBar from "./ReviewBar";

const AgentReviewSection = ({
  reviewSummary = {
    totalCount: 140,
    strengths: [
      {
        badgeId: 4,
        badgeCount: 102,
      },
    ],
  },
}: {
  reviewSummary?: {
    totalCount: number;
    strengths: { badgeId: number; badgeCount: number }[];
  };
}) => {
  const { t } = useTranslation(["pages"]);
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
      </div>
    </>
  );
};

export default AgentReviewSection;
