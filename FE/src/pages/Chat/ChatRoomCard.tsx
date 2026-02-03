import AlarmBadge from "../../assets/icon/AlarmBadge";
import Tag from "../../components/common/Tag";
import CalcLastChattedAt from "../../utils/CalcLastChattedAt";

type ChatRoomCardParams = {
  chatRoomId: number;
  profileImgUrl: string;
  opponentName: string;
  roomStatus: string;
  lastMessage: string;
  noneRead: number;
  lastChattedAt: string;
};

const ChatRoomCard = ({
  chatRoomId,
  profileImgUrl = "https://placehold.co/80x80",
  opponentName,
  roomStatus,
  lastMessage,
  noneRead,
  lastChattedAt,
}: ChatRoomCardParams) => {
  return (
    <div
      key={chatRoomId}
      className={`flex flex-row gap-6 items-center p-3 cursor-pointer`}
    >
      <img
        src={profileImgUrl}
        alt="행정사 프로필 사진"
        className="w-20 h-20 object-cover rounded-full"
      />
      <div className="flex flex-col gap-3 w-full    ">
        <div className="flex flex-row justify-between">
          <div>
            <span className="flex flex-row items-center gap-3 title-s-semibold text-text-base">
              {opponentName}
              {roomStatus === "MATCHED" ? (
                <Tag type="small_fill_icon" className="w-[83px]">
                  수임 확정
                </Tag>
              ) : roomStatus === "PROPOSED" ? (
                <div className="caption-l-medium text-violet-500">
                  수임 제안이 도착했어요!
                </div>
              ) : (
                <></>
              )}
            </span>
          </div>

          <span className="caption-l-regular text-text-sub">
            {CalcLastChattedAt(lastChattedAt)}
          </span>
        </div>
        <div className="flex flex-row items-center justify-between">
          <div className="body-s-regular text-text-sub w-[390px] line-clamp-2">
            {lastMessage}
          </div>
          {noneRead > 0 && (
            <AlarmBadge type="alarm_violet">
              {noneRead > 99 ? `99+` : noneRead}
            </AlarmBadge>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatRoomCard;
