import { Link } from "react-router-dom";
import BadgeIcon, { badgeDescription } from "../../assets/icon/BadgeIcon";
import { IcGraduation, IcLocation } from "../../assets/icon/StratisUi";
import Tag from "../../components/common/Tag";
import { jobList } from "../../types/job";

type searchAgentCardParams = {
  agent: {
    id: number;
    img: string;
    name: string;
    address: string;
    jobs: number[];
    badges: number[];
  };
};

const SearchAgentCard = ({
  agent = {
    id: 0,
    img: "https://placehold.co/140x140",
    name: "엄경례",
    address: "서울특별시 강남구",
    jobs: [1, 4, 6],
    badges: [3, 6],
  },
}: searchAgentCardParams) => {
  const authed = true; //클라이언트 전역 상태로 하면 될 듯
  return (
    <Link
      to={`/profile/agent/${agent.id}`}
      className="flex flex-row gap-8 py-6 px-7 bg-gray-30 w-124 h-fit rounded-2xl items-center"
    >
      <div className="w-fit h-fit rounded-full overflow-hidden">
        <img src={agent.img} className="h-35 w-35" />
      </div>
      <div className="flex flex-col gap-3">
        <div className="flex flex-row gap-3">
          {agent.badges.map((badgeId) => (
            <div className="flex flex-row gap-1 items-center caption-m-medium text-primary ">
              <BadgeIcon
                badgeIndex={badgeId}
                size={12}
                color="var(--primary)"
              />
              {badgeDescription[badgeId]}
            </div>
          ))}
        </div>
        <a className="title-m-bold -mt-2">{agent.name} 행정사</a>
        <div className="flex-col flex gap-1.5">
          <a className="flex flex-row items-center gap-1.5 caption-m-medium">
            <IcGraduation size={14} /> 전문 분야
          </a>
          {authed ? (
            <ol className="flex flex-row gap-1">
              {agent.jobs.slice(0, 2).map((jobId) => (
                <Tag type={"small_fill_violet_max"}>{jobList[jobId]}</Tag>
              ))}
              {agent.jobs.length > 2 && (
                <Tag type="small_fill_gray">+{agent.jobs.length - 2}</Tag>
              )}
            </ol>
          ) : (
            <Tag type={"small_fill_gray"}>로그인 후 확인 가능합니다.</Tag>
          )}
        </div>
        <div className="flex-col flex gap-1">
          <a className="flex flex-row items-center gap-1.5 caption-m-medium">
            <IcLocation size={14} /> 사무실 위치
          </a>
          <a className="text-text-base body-m-medium">{agent.address}</a>
        </div>
      </div>
    </Link>
  );
};

export default SearchAgentCard;
