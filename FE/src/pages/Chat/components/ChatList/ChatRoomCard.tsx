import AlarmBadge from "../../../../assets/icon/AlarmBadge";
import Tag from "../../../../components/common/Tag";
import CalcLastChattedAt from "../../../../utils/CalcLastChattedAt";
import { useAuth } from "../../../../contexts/AuthContextProvider";
import { IcPin } from "../../../../assets/icon/StratisUi";
import { useTranslation } from "react-i18next";
import { isUUID } from "../../../../utils/isUuid";

type ChatRoomCardParams = {
  profileImgUrl: string;
  opponentName: string;
  lastMessage: string;
  noneRead: number;
  lastChattedAt: string;
  proposed: boolean;
  proposalMatched: boolean;
};

const ChatRoomCard = ({
  profileImgUrl = "https://placehold.co/80x80",
  opponentName,
  lastMessage,
  noneRead,
  lastChattedAt,
  proposed,
  proposalMatched,
}: ChatRoomCardParams) => {
  const { t } = useTranslation(["components"]);
  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";
  return (
    <div className={`flex flex-row gap-6 items-center p-3 cursor-pointer`}>
      {isAgent ? (
        <div className="flex flex-row justify-center items-center w-20 aspect-square border border-border-normal rounded-full bg-violet-25 headline-l-bold text-violet-500">
          {opponentName[0]}
        </div>
      ) : (
        <img
          src={profileImgUrl}
          alt={t("chatRoom.attorneyProfileImage")}
          className="w-20 h-20 object-cover rounded-full flex-shrink-0"
        />
      )}
      <div className="flex flex-col gap-3 w-full    ">
        <div className="flex flex-row justify-between">
          <div>
            <span className="flex flex-row items-center gap-3 title-s-semibold text-text-base">
              {opponentName} {!isAgent && t("agentCard.title")}
              {proposalMatched ? (
                <Tag variant="small_fill_icon" className="min-w-[83px]">
                  <IcPin size="14" />
                  {t("chatRoom.retainerConfirmed")}
                </Tag>
              ) : (
                proposed && (
                  <div className="caption-l-medium text-violet-500">
                    {t("chatRoom.retainerProposalArrived")}
                  </div>
                )
              )}
            </span>
          </div>

          <span className="caption-l-regular text-text-sub">
            {CalcLastChattedAt(lastChattedAt)}
          </span>
        </div>
        <div className="flex flex-row items-center justify-between">
          <div className="body-s-regular text-text-sub w-[390px] line-clamp-2 h-[40px]">
            {isUUID(lastMessage) ? "MATCHED" : lastMessage}
          </div>
          {noneRead > 0 && (
            <AlarmBadge isActive={true}>
              {noneRead > 99 ? `99+` : noneRead}
            </AlarmBadge>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatRoomCard;
