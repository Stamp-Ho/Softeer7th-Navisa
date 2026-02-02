import { useParams } from "react-router-dom";
import { IcLuggage04 } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import Tag from "../../../components/common/Tag";
import ToolTipMessage from "../../../components/common/ToolTipMessage";
import ChatActivateModal from "./ChatActivateModal";
import { useEffect, useState } from "react";
import CalcLastAccessDay from "../../../utils/CalcLastAccessDay";

type ExpectedCompanyProps = {
  companyName: string;
  jobTitle: string;
  startDate: string;
  lastAccessDay: string;
  isChatting: boolean;
  nickName: string;
};

// D-Day 계산
const calcDDay = (targetDate: string): string => {
  const today = new Date();
  const normalized = targetDate.replace(/\.\s*/g, "-");
  const target = new Date(normalized);

  // 시/분/초 제거 (날짜 기준으로만 계산)
  today.setHours(0, 0, 0, 0);
  target.setHours(0, 0, 0, 0);

  const diffTime = target.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays > 0) return `D-${diffDays}`;

  if (diffDays === 0) return "D-Day";

  return `D+${Math.abs(diffDays)}`;
};

const ExpectedCompany = ({
  companyName,
  jobTitle,
  startDate,
  lastAccessDay,
  isChatting,
  nickName,
}: ExpectedCompanyProps) => {
  const { foreignerId } = useParams();
  const [viewMessageModal, setViewMessageModal] = useState(false);
  const [showToast, setShowToast] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowToast(false);
    }, 1500);
    return () => {
      clearTimeout(timer);
    };
  }, [showToast]);

  return (
    <>
      {viewMessageModal ? (
        <ChatActivateModal
          onClose={() => setViewMessageModal(false)}
          onSendSuccess={() => setShowToast(true)}
          isAgent={true}
        />
      ) : (
        <></>
      )}
      <div className="fixed right-48 shadow">
        <div className="flex flex-col w-92 px-5 py-8 border border-border-normal rounded-radius-400 bg-white">
          <div className="headline-l-bold text-text-base">
            {nickName} {foreignerId}
          </div>
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
              <span className="text-text-base title-s-medium">{jobTitle}</span>
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
                isChatting ? alert("gotochat") : setViewMessageModal(true)
              }
            >
              {isChatting ? "상담 이어하기" : "상담하기"}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ExpectedCompany;
