import ReviewBar from "./ReviewBar";

type AgentReviewProps = {
  totalReviews: number;
  strengths: StrengthsItem[];
};

type StrengthsItem = {
  badgeId: number;
  reviewCount: number;
};

const AgentReviewSection = ({ totalReviews, strengths }: AgentReviewProps) => {
  return (
    <>
      <div className="mb-13 headline-m-semibold text-gray-1000">
        총{" "}
        <span className="headline-m-bold text-violet-500">{totalReviews}</span>
        명의 의뢰인이 평가했어요
      </div>
      <div className="grid grid-cols-2 grid-rows-3 gap-4">
        {strengths.map((item) => (
          <ReviewBar
            key={item.badgeId}
            badgeId={item.badgeId}
            reviewCount={item.reviewCount}
            totalReviews={totalReviews}
          />
        ))}
      </div>
    </>
  );
};

export default AgentReviewSection;
