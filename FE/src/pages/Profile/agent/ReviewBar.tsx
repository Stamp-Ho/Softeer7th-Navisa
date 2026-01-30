import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";

type ReviewType = {
  badgeId: number;
  reviewCount: number;
  totalReviews: number;
};

const ReviewBar = ({ badgeId, reviewCount, totalReviews }: ReviewType) => {
  const ratio = totalReviews > 0 ? reviewCount / totalReviews : 0;
  const percentage = Math.min(100, Math.max(0, ratio * 100));
  const hasValidBadge = badgeId >= 0 && badgeId < badgeDescription.length;
  const label = hasValidBadge ? badgeDescription[badgeId] : "알 수 없음";
  return (
    <div className="relative flex flex-row justify-between items-center w-124 h-15 overflow-hidden border border-violet-50 rounded-[12px] bg-background-default">
      <div
        className={`absolute h-full bg-violet-50-transpar object-cover rounded-[8px]`}
        style={{
          width: `${percentage}%`,
        }}
      ></div>
      <div className="flex flex-row gap-3 pl-6 z-10">
        {hasValidBadge && <BadgeIcon badgeIndex={badgeId} />}
        <div className="body-l-semibold text-gray-700">
          {label}
        </div>
      </div>
      <div className="pr-6 title-s-semibold text-violet-400">{reviewCount}</div>
    </div>
  );
};

export default ReviewBar;
