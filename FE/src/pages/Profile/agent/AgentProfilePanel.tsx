import { useEffect, useState } from "react";
import Button from "../../../components/common/Button";
import ToolTipMessage from "../../../components/common/ToolTipMessage";
import CalcLastAccessDay from "../../../utils/CalcLastAccessDay";
import ChatActivateModal from "../Foreigner/ChatActivateModal";
import Toast from "../../../components/common/Toast";

type AgentProfilePanelProps = {
  profileImageUrl: string;
  name: string;
  officeName: string;
  lastAccessDay: string;
  isChatting: boolean;
};

const AgentProfilePanel = ({
  profileImageUrl,
  name,
  officeName,
  lastAccessDay,
  isChatting,
}: AgentProfilePanelProps) => {
  const [viewMessageModal, setViewMessageModal] = useState(false);
  const [showToast, setShowToast] = useState<boolean>(false);

  useEffect(() => {
    if (!showToast) return;
    const timer = setTimeout(() => {
      setShowToast(false);
    }, 1500);
    return () => {
      clearTimeout(timer);
    };
  }, [showToast]);

  return (
    <>
      {showToast && <Toast message="상담메시지가 전송되었습니다." />}
      {viewMessageModal ? (
        <ChatActivateModal
          onClose={() => {
            setViewMessageModal(false);
          }}
          onSendSuccess={() => {
            setShowToast(true);
          }}
          isAgent={false}
        />
      ) : (
        <></>
      )}

      <div className="flex flex-col items-center bg-white w-92 rounded-[20px] overflow-hidden shadow">
        <img
          className="w-187 h-113 object-cover"
          src={profileImageUrl || "https://placehold.co/748x462"}
          alt={`${name} 행정사 프로필 이미지`}
        />
        <div className="flex flex-col pt-6 pb-5 px-4 w-full">
          <div className="headline-l-bold text-text-base mb-3">
            {name} 행정사
          </div>
          <div className="title-s-medium text-text-base">{officeName}</div>
          <div className="flex flex-row justify-end">
            <ToolTipMessage message={CalcLastAccessDay(lastAccessDay)} />
          </div>
          <Button
            type="primary"
            className="w-full"
            onClick={() =>
              isChatting ? alert("gotochat") : setViewMessageModal(true)
            }
          >
            {isChatting ? "상담 이어하기" : "상담하기"}
          </Button>
        </div>
      </div>
    </>
  );
};

export default AgentProfilePanel;
