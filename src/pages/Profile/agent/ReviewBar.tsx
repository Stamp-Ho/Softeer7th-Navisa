import { useTranslation } from "react-i18next";
import BadgeIcon from "../../../assets/icon/BadgeIcon";
import { useBadgeLabels } from "../../../hooks/useLocalizationLists";

type ReviewType = {
  badgeId: number;
  reviewCount: number;
  totalReviews: number;
};

const ReviewBar = ({ badgeId, reviewCount, totalReviews }: ReviewType) => {
  const { t } = useTranslation(["components"]);
  const badgeLabels = useBadgeLabels();
  const ratio = totalReviews > 0 ? reviewCount / totalReviews : 0;
  const percentage = Math.min(100, Math.max(0, ratio * 100));
  const hasValidBadge = badgeId >= 0 && badgeId < badgeLabels.length;
  const label = hasValidBadge ? badgeLabels[badgeId] : t("agentProfile.unknownBadge");
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
        <div className="body-l-semibold text-gray-700">{label}</div>
      </div>
      <div className="pr-6 title-s-semibold text-violet-400">{reviewCount}</div>
    </div>
  );
};

export default ReviewBar;
