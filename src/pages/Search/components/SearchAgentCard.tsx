import { Link } from "react-router-dom";
import BadgeIcon from "../../../assets/icon/BadgeIcon";
import { IcGraduation, IcLocation } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";
import type { SearchAgentCardType } from "../../../types/Cards";
import { useEffect } from "react";
import { useResizeImage } from "../../../hooks/useResizeImage";
import { useTranslation } from "react-i18next";
import { useBadgeLabels, useJobCodeLabels } from "../../../hooks/useLocalizationLists";
import { useAuth } from "../../../contexts/AuthContextProvider";
import { alertT } from "../../../i18n/alerts";

/**
 * 
 * @param param0 
  agentId: number;
  agentName: string;
  profileImgUrl: string;
  officeAddress: string;
  agentSpecialityTop2: number[];
  badgeTop2: number[];
  specialityJobCount: number;
 * @returns 
 */
const SearchAgentCard = ({ agent }: { agent?: SearchAgentCardType }) => {
  const { t } = useTranslation(["components"]);
  const badgeLabels = useBadgeLabels();
  const jobCodeLabels = useJobCodeLabels();
  const { resizeImage, imageSize } = useResizeImage();
  const { userType } = useAuth();

  const authed = userType === "FILLED_FOREIGNER";
  useEffect(() => {
    if (agent?.profileImgUrl) resizeImage(agent.profileImgUrl, 140, 140);
  }, [agent]);
  if (!agent) return <SkeletonUI />;
  return (
    <div onClick={() => authed || alertT("components.agentCard.loginRequired")}>
      <Link
        to={authed ? `/profile/agent/${agent.agentId}` : "#"}
        className="flex flex-row items-center  gap-8 pl-7 bg-gray-30 w-124 h-fit rounded-2xl "
      >
        <div className="flex w-35 h-35 my-10 rounded-full overflow-hidden items-center justify-center shrink-0">
          <div className=" shrink-0">
            <img src={agent.profileImgUrl} width={imageSize.width} height={imageSize.height} />
          </div>
        </div>
        <div className="flex flex-col gap-3 py-3">
          <div className="flex flex-row gap-3">
            {agent.badgeTop2.length === 0 ? (
              <div className="flex flex-row gap-1 items-center caption-m-medium text-text-sub ">
                {t("agentCard.noReview")}
              </div>
            ) : (
              agent.badgeTop2.map((badgeId) => (
                <div
                  key={`badgeId_${badgeId - 1}`}
                  className="flex flex-row gap-1 items-center caption-m-medium text-primary "
                >
                  <BadgeIcon badgeIndex={badgeId - 1} size={12} color="var(--primary)" />
                  {badgeLabels[badgeId - 1]}
                </div>
              ))
            )}
          </div>
          <span className="title-m-bold -mt-2">
            {agent.agentName} {t("agentCard.title")}
          </span>
          <div className="flex-col flex gap-1.5">
            <span className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcGraduation size={14} /> {t("agentCard.expertise")}
              {agent.specialityJobCount && <span className="text-primary body-s-bold">{agent.specialityJobCount}</span>}
            </span>
            {authed && agent.agentSpecialityTop2 ? (
              <ol className="flex flex-row flex-wrap gap-1">
                {agent.agentSpecialityTop2.length === 0 && (
                  <Tag variant="small_fill_gray">{t("agentCard.noExpertise")}</Tag>
                )}
                {agent.agentSpecialityTop2.slice(0, 2).map((jobId) => (
                  <Tag key={`agent_special_job_${jobId - 1}`} variant={"small_fill_violet_max"}>
                    {jobCodeLabels[jobId - 1]}
                  </Tag>
                ))}
              </ol>
            ) : (
              <Tag variant={"small_fill_gray"}>{t("agentCard.loginRequired")}</Tag>
            )}
          </div>
          <div className="flex-col flex gap-1">
            <span className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcLocation size={14} /> {t("agentCard.office")}
            </span>
            <span className="text-text-base body-m-medium">{agent.officeAddress}</span>
          </div>
        </div>
      </Link>
    </div>
  );
};

export default SearchAgentCard;

const SkeletonUI = () => {
  return (
    <div className="flex flex-row items-center  gap-8 py-6 px-7 bg-gray-30 w-124 h-55 rounded-2xl ">
      <div className="w-35 h-35 rounded-full bg-gray-100" />
      <div className="flex flex-col gap-3">
        <div className="flex flex-row gap-3">
          {[1, 2].map((badgeId) => (
            <div key={`badgeId_${badgeId}`} className="flex flex-row gap-1 ">
              <Tag variant="tiny_skeleton" />
              <Tag variant="tiny_skeleton" className="w-15" />
            </div>
          ))}
        </div>
        <Tag variant="small_fill_gray" />
        <div className="flex-col flex gap-1.5">
          <div className="flex flex-row  gap-1.5">
            <Tag variant="tiny_skeleton" />
            <Tag variant="tiny_skeleton" className="w-17" />
          </div>

          <div className="flex flex-row gap-1">
            <Tag variant={"small_fill_gray"} className="w-13" />
            <Tag variant={"small_fill_gray"} className="w-13" />
          </div>
        </div>
        <div className="flex-col flex gap-1">
          <div className="flex flex-row  gap-1.5">
            <Tag variant="tiny_skeleton" />
            <Tag variant="tiny_skeleton" className="w-17" />
          </div>
          <Tag variant="tiny_skeleton" className="w-17" />
        </div>
      </div>
    </div>
  );
};
