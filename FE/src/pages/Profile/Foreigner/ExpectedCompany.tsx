import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { IcLuggage04 } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import Tag from "../../../components/common/Tag";
import ToolTipMessage from "../../../components/common/ToolTipMessage";
import ChatActivateModal from "./ChatActivateModal";
import CalcLastAccessDay from "../../../utils/CalcLastAccessDay";
import Toast from "../../../components/common/Toast";
import { calcDDay } from "../../../utils/CalcDDay";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../contexts/AuthContextProvider";

const ExpectedCompany = ({
  targetJob = "웹 개발자",
  companyName = "대박쩌는 IT회사",
  startDate = "2026. 01. 31",
  nickname = "고라니 099",
  lastAccessDay = "2026-01-26T11:27:02+09:00",
  hasChatRoomBetween = false,
  opponentProfileId = "",
}: {
  targetJob?: string;
  companyName?: string;
  startDate?: string;
  nickname?: string;
  lastAccessDay?: string;
  hasChatRoomBetween?: boolean;
  chatRoomId?: number;
  opponentProfileId?: string;
}) => {
  const { t } = useTranslation(["pages"]);
  const [viewMessageModal, setViewMessageModal] = useState(false);
  const [showToast, setShowToast] = useState(false);
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
      {showToast && <Toast message={t("chat.consultMessageSent")} />}
      {viewMessageModal && (
        <ChatActivateModal
          onClose={() => setViewMessageModal(false)}
          opponentProfileId={opponentProfileId}
          onSendSuccess={() => setShowToast(true)}
          isAgent={isAgent}
        />
      )}
      <div className="">
        <div className="shadow flex flex-col w-92 px-5 py-8 border border-border-normal rounded-radius-400 bg-white">
          <div className="headline-l-bold text-text-base">{nickname}</div>
          <div className="py-px w-full bg-border-light my-7"></div>
          <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
            <IcLuggage04 />
            <span>{t("profile.expectedJoinInfo")}</span>
          </div>
          <ul className="flex flex-col gap-3 mt-7">
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.jobLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{targetJob}</span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.companyNameLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{companyName}</span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag variant="large_gray_off" className="w-22">
                {t("profile.joinDateLabel")}
              </Tag>
              <span className="text-text-base title-s-medium">{startDate}</span>
              <span className="body-l-medium text-text-sub">{calcDDay(startDate)}</span>
            </li>
          </ul>
          <div className="flex flex-col items-end mt-4.25">
            <ToolTipMessage message={CalcLastAccessDay(lastAccessDay)} />
            <Button
              variant="primary"
              size="large"
              className="w-full"
              onClick={() => (hasChatRoomBetween ? navigate(`/chat`) : setViewMessageModal(true))}
            >
              {hasChatRoomBetween ? t("profile.continueConsult") : t("profile.consult")}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ExpectedCompany;
