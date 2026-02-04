import BadgeIcon, { badgeDescription } from "../../assets/icon/BadgeIcon";

type BadgeReviewSectionParams = {
  title: string;
  badgeArray: number[];
  selectedBadges: number[];
  onToggleBadges: (id: number) => void;
};

const BadgeReviewSection = ({
  title,
  badgeArray,
  selectedBadges,
  onToggleBadges,
}: BadgeReviewSectionParams) => {
  return (
    <>
      <div className="mb-4 body-l-semibold text-text-base">{title}</div>
      <div className="flex flex-row gap-3 flex-wrap">
        {badgeArray.map((num) => {
          const isActive = selectedBadges.includes(num);

          return (
            <button
              key={num}
              onClick={() => onToggleBadges(num)}
              className={`flex flex-row gap-2 items-center p-3 rounded-[8px] body-m-medium cursor-pointer ${isActive ? "bg-violet-50 text-primary" : "bg-gray-50 text-text-base"}`}
            >
              <BadgeIcon
                badgeIndex={num}
                color={isActive ? "var(--primary)" : "var(--gray-800)"}
                size={18}
              />
              {badgeDescription[num]}
            </button>
          );
        })}
      </div>
    </>
  );
};
export default BadgeReviewSection;
