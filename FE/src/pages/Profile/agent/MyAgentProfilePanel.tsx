import { useTranslation } from "react-i18next";
import Button from "../../../components/common/Button";
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

const MyAgentProfilePanel = ({ agentInfo, officeName }: AgentProfilePanelProps) => {
  const { t } = useTranslation(["components"]);
  const navigate = useNavigate();

  const { logOut } = useAuth();
  if (!agentInfo || !officeName) return <SkeletonUi />;

  return (
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
        <div className="flex flex-col items-end gap-6 mt-4.25">
          <Button variant="primary" className="w-full" size="large" onClick={() => navigate(`/update/profile`)}>
            {t("agentProfile.editMyInfo")}
          </Button>
          <Button variant="lightGray" size="large" className="w-full " onClick={logOut}>
            {t("agentProfile.logOut")}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default MyAgentProfilePanel;

const SkeletonUi = () => {
  const { t } = useTranslation(["components"]);
  const navigate = useNavigate();
  const { logOut } = useAuth();
  return (
    <div className="flex flex-col items-center bg-white w-92 rounded-[20px] overflow-hidden shadow">
      <div className="flex flex-col pt-6 pb-5 px-4 w-full">
        <div className="title-l-bold text-text-base mb-3">{t("agentProfile.agentInvalid")}</div>
        <div className="py-px w-full bg-border-light" />
        <div className="flex flex-col items-end gap-6 mt-4.25">
          <Button
            variant="primary"
            className="w-full"
            size="large"
            onClick={() => navigate(`/onboard/agent`, { replace: true })}
          >
            {t("agentProfile.enterMyInfo")}
          </Button>
          <Button variant="lightGray" size="large" className="w-full " onClick={logOut}>
            {t("agentProfile.logOut")}
          </Button>
        </div>
      </div>
    </div>
  );
};
