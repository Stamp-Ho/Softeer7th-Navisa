import { IcLuggage04 } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import Tag from "../../../components/common/Tag";
import ToolTipMessage from "../../../components/common/ToolTipMessage";
import ChatActivateModal from "./ChatActivateModal";
import { useContext, useEffect, useState } from "react";
import CalcLastAccessDay from "../../../utils/CalcLastAccessDay";
import { AuthContext } from "../../../contexts/AuthContext";
import Toast from "../../../components/common/Toast";
import { calcDDay } from "../../../utils/CalcDDay";

const ExpectedCompany = ({
  targetJob = "웹 개발자",
  companyName = "대박쩌는 IT회사",
  startDate = "2026. 01. 31",
  nickname = "고라니 099",
  lastAccessDay = "2026-01-26T11:27:02+09:00",
  hasChatRoomBetween = false,
  chatRoomId = 0,
}: {
  targetJob?: string;
  companyName?: string;
  startDate?: string;
  nickname?: string;
  lastAccessDay?: string;
  hasChatRoomBetween?: boolean;
  chatRoomId?: number;
}) => {
  const [viewMessageModal, setViewMessageModal] = useState(false);
  const [showToast, setShowToast] = useState(false);

  useEffect(() => {
    if (!showToast) return;
    const timer = setTimeout(() => {
      setShowToast(false);
    }, 1500);
    return () => {
      clearTimeout(timer);
    };
  }, [showToast]);

  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;
  const isAgent = userType === "VALID_AGENT";

  return (
    <>
      {showToast && <Toast message="상담메시지가 전송되었습니다." />}
      {viewMessageModal ? (
        <ChatActivateModal
          onClose={() => setViewMessageModal(false)}
          onSendSuccess={() => setShowToast(true)}
          isAgent={isAgent}
        />
      ) : (
        <></>
      )}
      <div className="fixed right-48 shadow">
        <div className="flex flex-col w-92 px-5 py-8 border border-border-normal rounded-radius-400 bg-white">
          <div className="headline-l-bold text-text-base">{nickname}</div>
          <div className="py-0.25 w-full bg-border-light my-7"></div>
          <div className="flex flex-row gap-2 items-center title-m-semibold text-text-base">
            <IcLuggage04 />
            <span>입사 예정 정보</span>
          </div>
          <ul className="flex flex-col gap-3 mt-7">
            <li className="flex flex-row gap-4 items-center">
              <Tag type="large_gray_off" className="w-22">
                직무
              </Tag>
              <span className="text-text-base title-s-medium">{targetJob}</span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag type="large_gray_off" className="w-22">
                회사명
              </Tag>
              <span className="text-text-base title-s-medium">
                {companyName}
              </span>
            </li>
            <li className="flex flex-row gap-4 items-center">
              <Tag type="large_gray_off" className="w-22">
                입사 날짜
              </Tag>
              <span className="text-text-base title-s-medium">{startDate}</span>
              <span className="body-l-medium text-text-sub">
                {calcDDay(startDate)}
              </span>
            </li>
          </ul>
          <div className="flex flex-col items-end mt-2.25">
            <ToolTipMessage message={CalcLastAccessDay(lastAccessDay)} />
            <Button
              type="primary"
              size="large"
              className="w-full"
              onClick={() =>
                hasChatRoomBetween
                  ? alert("gotochat")
                  : setViewMessageModal(true)
              }
            >
              {hasChatRoomBetween ? "상담 이어하기" : "상담하기"}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ExpectedCompany;
