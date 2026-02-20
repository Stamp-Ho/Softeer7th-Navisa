import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Button from "../../../components/common/Button";
import ToolTipMessage from "../../../components/common/ToolTipMessage";
import CalcLastAccessDay from "../../../utils/CalcLastAccessDay";
import ChatActivateModal from "../Foreigner/ChatActivateModal";
import Toast from "../../../components/common/Toast";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContextProvider";

type AgentProfilePanelProps = {
  agentInfo?: {
    name: string;
    profileImageUrl: string;
    lastLoginAt: string;
    hasChatRoom: boolean;
    hasBlocked: boolean;
    chatRoomId: number;
  };
  officeName?: string;
  opponentProfileId?: string;
};

const AgentProfilePanel = ({
  agentInfo = {
    name: "엄경례",
    profileImageUrl: "https://placehold.co/368x452",
    lastLoginAt: "2026-01-28T11:27:02+09:00",
    hasChatRoom: false,
    hasBlocked: false,
    chatRoomId: 0,
  },
  officeName = "엄경례 행정사사무소",
  opponentProfileId = "",
}: AgentProfilePanelProps) => {
  const { t } = useTranslation(["components"]);
  const [viewMessageModal, setViewMessageModal] = useState(false);
  const [showToast, setShowToast] = useState<boolean>(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!showToast) return;
    const timer = setTimeout(() => {
      setShowToast(false);
    }, 1500);
    return () => {
      clearTimeout(timer);
    };
  }, [showToast]);

  const { userType } = useAuth();
  const isAgent = userType === "VALID_AGENT";

  return (
    <>
      {showToast && <Toast message={t("agentProfile.consultMessageSent")} />}
      {viewMessageModal ? (
        <ChatActivateModal
          onClose={() => {
            setViewMessageModal(false);
          }}
          onSendSuccess={() => {
            setShowToast(true);
          }}
          // chatRoomId={0}
          opponentProfileId={opponentProfileId}
          isAgent={isAgent}
        />
      ) : (
        <></>
      )}

      <div className="flex flex-col items-center bg-white w-92 rounded-[20px] overflow-hidden shadow">
        <img
          className="w-187 h-113 object-cover"
          src={agentInfo.profileImageUrl || "https://placehold.co/748x462"}
          alt={t("agentProfile.profileImageAlt", { name: agentInfo.name })}
        />
        <div className="flex flex-col pt-6 pb-5 px-4 w-full">
          <div className="headline-l-bold text-text-base mb-3">
            {agentInfo.name} {t("agentProfile.title")}
          </div>
          <div className="title-s-medium text-text-base">{officeName}</div>
          <div className="flex flex-row justify-end mb-4">
            <ToolTipMessage message={CalcLastAccessDay(agentInfo.lastLoginAt)} />
          </div>
          <Button
            variant="primary"
            className="w-full"
            onClick={() => (agentInfo.hasChatRoom ? navigate(`/chat`) : setViewMessageModal(true))}
          >
            {agentInfo.hasChatRoom ? t("agentProfile.continueConsult") : t("agentProfile.consult")}
          </Button>
        </div>
      </div>
    </>
  );
};

export default AgentProfilePanel;
