import Tag from "../../../../components/common/Tag";
import AlarmBadge from "../../../../assets/icon/AlarmBadge";

interface ChatRoomTabButtonProps {
  label: string;
  value: "all" | "unread" | "matched";
  selectedTab: "all" | "unread" | "matched";
  onClick: () => void;
  count?: number; // 알림 배지용 (optional)
  width?: string;
}

const ChatRoomTabButton = ({
  label,
  value,
  selectedTab,
  onClick,
  count,
  width = "auto",
}: ChatRoomTabButtonProps) => {
  const isSelected = selectedTab === value;

  const getTagType = () => {
    if (value === "all") {
      return isSelected ? "large_violet_off" : "large_white_off";
    }
    // unread, matched 공통 스타일
    return isSelected ? "large_violet_on_alarm" : "large_white_on_alarm";
  };

  return (
    <div className="cursor-pointer" onClick={onClick}>
      <Tag variant={getTagType()} className={width}>
        {label}
        {count !== undefined ? (
          count > 0 ? (
            <AlarmBadge isActive={isSelected}>
              {count > 99 ? "99+" : count}
            </AlarmBadge>
          ) : (
            <div className="pr-1" />
          )
        ) : (
          <></>
        )}
      </Tag>
    </div>
  );
};

export default ChatRoomTabButton;
