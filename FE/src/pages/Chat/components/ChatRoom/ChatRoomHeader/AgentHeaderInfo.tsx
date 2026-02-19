import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import BadgeIcon, {
  badgeDescription,
} from "../../../../../assets/icon/BadgeIcon";
import { IcArrows } from "../../../../../assets/icon/StratisUi";
import type { AgentInfo } from "../../../../../types/chatRoomTypes";

type Props = {
  data?: AgentInfo;
};

const AgentHeaderInfo = ({ data }: Props) => {
  const { t } = useTranslation(["components"]);
  const content = (
    <div className="flex flex-row items-center title-m-bold text-text-base">
      <div className="flex flex-col justify-between">
        <div className="flex flex-row gap-3">
          {data?.top2BadgeIds?.map((badgeId) => (
            <div
              key={badgeId}
              className="flex flex-row items-center gap-1 caption-m-medium text-violet-500"
            >
              <BadgeIcon
                badgeIndex={badgeId}
                color="var(--violet-500)"
                size={12}
              />
              {badgeDescription[badgeId]}
            </div>
          ))}
        </div>
        <div className="flex flex-row items-center gap-1 title-m-bold text-text-base">
          {data?.name}{t("chatRoom.attorneySuffix")}
          <div className="flex items-center -rotate-90 cursor-pointer">
            <IcArrows size={20} />
          </div>
        </div>
      </div>
    </div>
  );

  return data?.agentId ? (
    <Link to={`/profile/agent/${data.agentId}`}>{content}</Link>
  ) : (
    content
  );
};

export default AgentHeaderInfo;
