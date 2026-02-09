import { Link } from "react-router-dom";
import BadgeIcon, { badgeDescription } from "../../../assets/icon/BadgeIcon";
import { IcGraduation, IcLocation } from "../../../assets/icon/StratisUi";
import Tag from "../../../components/common/Tag";
import { jobList } from "../../../constants/job";
import type { SearchAgentCardType } from "../../../types/Cards";
import { useContext } from "react";
import { AuthContext } from "../../../contexts/AuthContext";

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
const SearchAgentCard = ({
  agent = {
    agentId: "0",
    profileImgUrl: "https://placehold.co/140x140",
    agentName: "엄경례",
    officeAddress: "서울특별시 강남구",
    agentSpecialityTop2: [1, 4, 6],
    badgeTop2: [3, 6],
    specialityJobCount: 0,
  },
}: {
  agent: SearchAgentCardType;
}) => {
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;

  const authed = userType !== "NOT_AUTHED";

  return (
    <div>
      <Link
        to={`/profile/agent/${agent.agentId}`}
        className="flex flex-row gap-8 py-6 px-7 bg-gray-30 w-124 h-fit rounded-2xl items-center"
      >
        <div className="w-fit h-fit rounded-full overflow-hidden">
          <img src={agent.profileImgUrl} className="h-35 w-35" />
        </div>
        <div className="flex flex-col gap-3">
          <div className="flex flex-row gap-3">
            {agent.badgeTop2.map((badgeId) => (
              <div
                key={`badgeId_${badgeId}`}
                className="flex flex-row gap-1 items-center caption-m-medium text-primary "
              >
                <BadgeIcon
                  badgeIndex={badgeId}
                  size={12}
                  color="var(--primary)"
                />
                {badgeDescription[badgeId]}
              </div>
            ))}
          </div>
          <span className="title-m-bold -mt-2">{agent.agentName} 행정사</span>
          <div className="flex-col flex gap-1.5">
            <span className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcGraduation size={14} /> 전문 분야
            </span>
            {authed ? (
              <ol className="flex flex-row gap-1">
                {agent.agentSpecialityTop2.slice(0, 2).map((jobId) => (
                  <Tag type={"small_fill_violet_max"}>{jobList[jobId]}</Tag>
                ))}
                {agent.agentSpecialityTop2.length > 2 && (
                  <Tag type="small_fill_gray">
                    +{agent.agentSpecialityTop2.length - 2}
                  </Tag>
                )}
              </ol>
            ) : (
              <Tag type={"small_fill_gray"}>로그인 후 확인 가능합니다.</Tag>
            )}
          </div>
          <div className="flex-col flex gap-1">
            <span className="flex flex-row items-center gap-1.5 caption-m-medium">
              <IcLocation size={14} /> 사무실 위치
            </span>
            <span className="text-text-base body-m-medium">
              {agent.officeAddress}
            </span>
          </div>
        </div>
      </Link>
    </div>
  );
};

export default SearchAgentCard;
