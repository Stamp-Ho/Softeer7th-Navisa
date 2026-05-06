import { useTranslation } from "react-i18next";
import BadgeIcon from "../../../assets/icon/BadgeIcon";

type BadgeChipProps = {
  badgeId: number;
  isActive: boolean;
};

const BadgeChip = ({ badgeId, isActive }: BadgeChipProps) => {
  const { t } = useTranslation(["common"]);

  return (
    <div
      className={`${isActive ? "bg-violet-50-transpar text-violet-500" : "text-text-sub"} body-l-semibold flex flex-row gap-1 items-center h-11 px-3 py-2 rounded-full cursor-pointer`}
    >
      {isActive && <BadgeIcon badgeIndex={badgeId} color="var(--violet-500)" size={20} />}
      {t(`badges.${badgeId}`)}
    </div>
  );
};

export default BadgeChip;
