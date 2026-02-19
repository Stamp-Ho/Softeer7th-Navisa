import { Link } from "react-router-dom";
import { IcGraduation, IcLocation } from "../../assets/icon/StratisUi";
import Tag from "../common/Tag";
import { useContext, useEffect } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import type { AgentCardResponse } from "../../api/types/agent";
import { useResizeImage } from "../../hooks/useResizeImage";
import { useJobListLabels } from "../../assets/JobIcon";
import { useTranslation } from "react-i18next";

const AgentCard = ({ hasAnimation = true, agent, className = "" }: { hasAnimation?: boolean; agent?: AgentCardResponse; className: string }) => {
  const context = useContext(AuthContext);
  const { t } = useTranslation(["components"]);
  const jobListLabels = useJobListLabels();
  const { resizeImage, imageSize, loadingImage } = useResizeImage();

  const animationStyle = hasAnimation ? "transition-all duration-150 ease-out hover:scale-107 hover:m-2" : "";

  useEffect(() => {
    if (agent?.profileImgUrl) resizeImage(agent.profileImgUrl, 240, 192);
  }, [agent]);
  if (!context || !agent || loadingImage) return SkeletonUi();
  const { userType } = context;
  return (
    <li
      className={`${animationStyle} ${className}
        flex flex-col bg-white w-60 rounded-[10px] overflow-hidden shadow-[0px_0px_7px_0px_rgba(104,96,160,0.25)]`}
    >
      <Link to={`/profile/agent/${agent.agentId}`}>
        <div className="flex w-60 h-48 overflow-hidden items-center justify-center">
          <div className="shrink-0">
            <img src={agent.profileImgUrl} width={imageSize.width} height={imageSize.height} />
          </div>
        </div>
        <div className="flex flex-col gap-3 pb-5 px-4 h-44.75">
          <h4 className="title-m-bold pt-5">{agent.agentName} {t("agentCard.title")}</h4>
          <div className="flex-col flex gap-1">
            <a className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcGraduation size={14} /> {t("agentCard.expertise")}
            </a>
            {userType !== "NOT_AUTHED" ? (
              <ol className="flex flex-row gap-1">
                {agent.agentSpecialityTop2?.length === 0 && <Tag variant="small_fill_gray">{t("agentCard.noExpertise")}</Tag>}
                {agent.agentSpecialityTop2?.slice(0, 2).map((jobId) => (
                  <Tag key={`agent_special_job_${jobId}`} variant={"small_fill_violet_max"}>
                    {jobListLabels[jobId]}
                  </Tag>
                ))}
                {(agent.agentSpecialityTop2?.length ?? 0) > 2 && <Tag variant="small_fill_gray">{(agent.agentSpecialityTop2?.length ?? 0) - 2}</Tag>}
              </ol>
            ) : (
              <Tag variant={"small_fill_gray"} className="w-fit">
                {t("agentCard.loginRequired")}
              </Tag>
            )}
          </div>
          <div className="flex-col flex gap-1">
            <a className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcLocation size={14} /> {t("agentCard.office")}
            </a>
            <a className="text-text-base body-m-medium">{agent.officeAddress}</a>
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
      className={`transition-all duration-150 ease-out hover:scale-107 hover:m-2 ${className}
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
