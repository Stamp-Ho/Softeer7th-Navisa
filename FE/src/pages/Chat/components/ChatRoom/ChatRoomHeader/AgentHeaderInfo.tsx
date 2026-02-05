import BadgeIcon, {
  badgeDescription,
} from "../../../../../assets/icon/BadgeIcon";
import { IcArrows } from "../../../../../assets/icon/StratisUi";
import type { AgentInfo } from "../../../../../types/chatRoomTypes";

type Props = {
  data: AgentInfo;
};

const AgentHeaderInfo = ({ data }: Props) => {
  return (
    <div className="flex flex-col justify-between">
      <div className="flex flex-row gap-3">
        {data.strengths.slice(0, 2).map((strengths) => (
          <div
            key={strengths.badgeId}
            className="flex flex-row items-center gap-1 caption-m-medium text-violet-500"
          >
            <BadgeIcon
              badgeIndex={strengths.badgeId}
              color="var(--violet-500)"
              size={12}
            />
            {badgeDescription[strengths.badgeId]}
          </div>
        ))}
      </div>
      <div className="flex flex-row items-center gap-1 title-m-bold text-text-base">
        {data.agentInfo.name} 행정사
        <div
          className="flex items-center -rotate-90 cursor-pointer"
          onClick={() => alert(data.agentInfo.agentId)}
        >
          <IcArrows size={20} />
        </div>
      </div>
    </div>
  );
};

export default AgentHeaderInfo;
