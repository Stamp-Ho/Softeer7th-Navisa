import { Link } from "react-router-dom";
import { IcGraduation, IcLocation } from "../../assets/icon/StratisUi";
import Tag from "../common/Tag";
import { useContext, useEffect } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import type { AgentCardResponse } from "../../api/types/agent";
import { useResizeImage } from "../../hooks/useResizeImage";
import { useTranslation } from "react-i18next";
import BadgeIcon, { badgeDescription } from "../../assets/icon/BadgeIcon";
import { jobCodeList } from "../../constants/job";

const AgentCard = ({
  hasAnimation = true,
  agent,
  className = "",
  tabIndex = 0,
  disabled = false,
}: {
  hasAnimation?: boolean;
  agent?: AgentCardResponse;
  className: string;
  tabIndex?: number;
  disabled?: boolean;
}) => {
  const context = useContext(AuthContext);
  const { t } = useTranslation(["components"]);
  const { resizeImage, imageSize, loadingImage } = useResizeImage();

  const animationStyle = hasAnimation ? "transition-all duration-75 ease-out hover:scale-107 hover:m-2" : "";

  useEffect(() => {
    if (agent?.profileImgUrl) resizeImage(agent.profileImgUrl, 240, 192);
  }, [agent]);
  if (!context || !agent || loadingImage) return SkeletonUi();
  const { userType } = context;
  return (
    <li
      className={`${animationStyle} ${className}
        focus-within:ring-3 focus-within:ring-primary
        flex flex-col bg-white w-60 rounded-[10px] overflow-hidden shadow-[0px_0px_7px_0px_rgba(104,96,160,0.25)]`}
      tabIndex={-1}
      inert={disabled ? true : undefined}
    >
      <Link to={`/profile/agent/${agent.agentId}`} tabIndex={tabIndex}>
        <div className="flex w-60 h-48 overflow-hidden items-center justify-center">
          <div className="shrink-0">
            <img src={agent.profileImgUrl} width={imageSize.width} height={imageSize.height} />
          </div>
        </div>
        <div className="flex flex-col pb-4 pt-2 px-4 h-46.75">
          <div className="flex flex-row gap-3 mt-1">
            {agent.badgeTop2.length === 0 ? (
              <div className="flex flex-row gap-1 items-center caption-s-medium text-text-sub ">
                등록된 리뷰가 없습니다
              </div>
            ) : (
              agent.badgeTop2.map((badgeId) => (
                <div
                  key={`badgeId_${badgeId - 1}`}
                  className="flex flex-row gap-1 items-center caption-s-medium text-primary "
                >
                  <BadgeIcon badgeIndex={badgeId - 1} size={12} color="var(--primary)" />
                  {badgeDescription[badgeId - 1]}
                </div>
              ))
            )}
          </div>
          <h4 className="title-m-bold ">
            {agent.agentName} {t("agentCard.title")}
          </h4>
          <div className="flex-col flex gap-1 max-h-21 min-h-17">
            <h5 className="flex flex-row items-center gap-1.5 caption-m-medium mt-auto">
              <IcGraduation size={14} /> {t("agentCard.expertise")}
            </h5>
            {userType !== "NOT_AUTHED" ? (
              <ol className="flex flex-row gap-1 flex-wrap">
                {(agent.agentSpecialityTop2?.length === 0 || !agent.agentSpecialityTop2) && (
                  <Tag variant="small_fill_gray">{t("agentCard.noExpertise")}</Tag>
                )}
                {agent.agentSpecialityTop2?.slice(0, 2).map((jobId) => (
                  <Tag key={`agent_special_job_${jobId}`} variant={"small_fill_violet_max"}>
                    {jobCodeList[jobId - 1]}
                  </Tag>
                ))}
                {(agent.agentSpecialityTop2?.length ?? 0) > 2 && (
                  <Tag variant="small_fill_gray">{(agent.agentSpecialityTop2?.length ?? 0) - 2}</Tag>
                )}
              </ol>
            ) : (
              <Tag variant={"small_fill_gray"} className="w-fit">
                {t("agentCard.loginRequired")}
              </Tag>
            )}
            <div className="mb-auto" />
          </div>
          <div className="flex-col flex">
            <h5 className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcLocation size={14} /> {t("agentCard.office")}
            </h5>
            <p className="text-text-base body-m-medium">{agent.officeAddress}</p>
          </div>
        </div>
      </Link>
    </li>
  );
};

export default AgentCard;

const SkeletonUi = (className = "") => {
  return (
    <li
      className={`transition-all duration-75 ease-out hover:scale-107 hover:m-2 ${className}
        flex flex-col bg-white w-60 rounded-[10px] overflow-hidden shadow-[0px_0px_7px_0px_rgba(104,96,160,0.25)]`}
    >
      <div className="w-60 h-48 bg-gray-100" />
      <div className="flex flex-col gap-3 pb-5 px-4">
        <Tag variant="small_fill_gray" className="w-28 mt-5" />
        <div className="flex-col flex gap-1">
          <a className="flex flex-row items-center gap-1.5">
            <Tag variant="tiny_skeleton" />
            <Tag variant="tiny_skeleton" className="w-17" />
          </a>
          <Tag variant="small_fill_gray" className="w-40 mt-1" />
        </div>
        <div className="flex-col flex gap-1">
          <a className="flex flex-row items-center gap-1.5">
            <Tag variant="tiny_skeleton" />
            <Tag variant="tiny_skeleton" className="w-17" />
          </a>
          <Tag variant="tiny_skeleton" className="w-50 mt-2" />
        </div>
      </div>
    </li>
  );
};
