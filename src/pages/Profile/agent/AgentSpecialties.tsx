import { useTranslation } from "react-i18next";
import { IcGraduation } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";
import ProfileItemsFrame from "../../../components/common/ProfileItemsFrame";
import { useJobCodeLabels } from "../../../hooks/useLocalizationLists";

const AgentSpecialties = ({ jobCodeIds }: { jobCodeIds?: number[] }) => {
  const { t } = useTranslation(["pages"]);
  const jobCodeLabels = useJobCodeLabels();
  if (!jobCodeIds) return <SkeletonUi t={t} />;
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base">
        <IcGraduation size={24} />
        <span>{t("profile.specialty")}</span>
        <span className="text-violet-500">{jobCodeIds.length}</span>
      </div>
      <ul className="flex flex-row gap-2 flex-wrap">
        {jobCodeIds.map((code, idx) => (
          <li key={idx}>
            <Tag variant="large_violet_off">{jobCodeLabels[code - 1]}</Tag>
          </li>
        ))}
      </ul>
    </ProfileItemsFrame>
  );
};

export default AgentSpecialties;

const SkeletonUi = ({ t }: { t: (key: string) => string }) => {
  return (
    <ProfileItemsFrame>
      <div className="flex flex-row gap-2 title-m-semibold text-text-base">
        <IcGraduation size={24} />
        <span>{t("profile.specialty")}</span>
        <span className="text-violet-500">0</span>
      </div>
      <ul className="flex flex-row gap-2 flex-wrap">
        <li>
          <Tag variant="large_violet_off" className="animate-pulse">
            {t("profile.specialtyPlaceholder")}
          </Tag>
        </li>
      </ul>
    </ProfileItemsFrame>
  );
};
