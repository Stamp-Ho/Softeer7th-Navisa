import BadgeIcon, { badgeDescription } from "../../assets/icon/BadgeIcon";
import { IcArrows, IcX } from "../../assets/icon/StratisUi";
import Button from "../../components/common/Button";
import Chip from "../../components/common/Chip";
import { nationList } from "../../types/nations";

// 필요 데이터
// 행정사
const dummyAgent = {
  agentInfo: {
    agentId: 10,
    name: "엄경례",
    profileImageUrl: "https://placehold.co/748x462",
  },
  strengths: [
    { badgeId: 4, reviewCount: 102 },
    { badgeId: 10, reviewCount: 79 },
    { badgeId: 2, reviewCount: 73 },
    { badgeId: 7, reviewCount: 50 },
    { badgeId: 1, reviewCount: 21 },
    { badgeId: 0, reviewCount: 7 },
  ],
};

// 외국인
const dummyforeigner = {
  basicInfo: {
    foreignerId: 99,
    nickname: "고라니 099",
    nationIdList: [1, 24],
  },
  expectedInfo: {
    targetJob: "웹 개발자",
    companyName: "대박쩌는 IT회사",
    startDate: "2026. 01. 31",
  },
};

type HeaderParams = {
  amAgent: boolean;
  isMatched: boolean;
  onClose: () => void;
  onModalAction: (num: number) => void;
};

const ChatRoomHeader = ({
  amAgent,
  isMatched,
  onClose,
  onModalAction,
}: HeaderParams) => {
  return (
    <>
      <div className="absolute w-full top-0">
        {/* 채팅창 헤더, 외국인인가 행정사인가에 따라 출력 달라짐 */}
        <div className="flex flex-row justify-between items-center p-6 bg-white/80 backdrop-blur-[2px]">
          <div className="w-[500px]">
            {amAgent ? (
              <div className="flex flex-col justify-between">
                <div className="flex flex-row gap-3">
                  {dummyAgent.strengths.slice(0, 2).map((data) => (
                    <div
                      key={data.badgeId}
                      className="flex flex-row items-center gap-1 caption-m-medium text-violet-500"
                    >
                      <BadgeIcon
                        badgeIndex={data.badgeId}
                        color="var(--violet-500)"
                        size={12}
                      />
                      {badgeDescription[data.badgeId]}
                    </div>
                  ))}
                </div>
                <div className="flex flex-row items-center gap-1 title-m-bold text-text-base">
                  {dummyAgent.agentInfo.name} 행정사
                  <div className="flex items-center -rotate-90 cursor-pointer">
                    <IcArrows size="20" />
                  </div>
                </div>
              </div>
            ) : (
              <div className="flex flex-row items-center title-m-bold text-text-base">
                {dummyforeigner.basicInfo.nickname}
                <div className="pl-[1px] h-8 mx-4 bg-border-normal" />
                <div className="flex flex-col justify-between">
                  <div className="flex flex-row items-center">
                    <span className="mr-3 body-l-semibold">
                      {dummyforeigner.expectedInfo.targetJob}
                    </span>
                    <span className="mr-1 caption-m-medium">
                      {dummyforeigner.expectedInfo.startDate}
                    </span>
                    <span className="caption-m-medium">입사 예정</span>
                  </div>
                  <div className="flex flex-row gap-1 items-center caption-m-medium text-text-sub">
                    {dummyforeigner.basicInfo.nationIdList.map((nationIdx) => (
                      <span key={nationIdx}>{nationList[nationIdx]}</span>
                    ))}
                  </div>
                </div>
                <div className="flex items-center -rotate-90 cursor-pointer ml-6">
                  <IcArrows size="20" />
                </div>
              </div>
            )}
          </div>

          <div className="flex flex-row">
            {isMatched ? (
              <div onClick={() => onModalAction(2)}>
                <Chip type="chips_square_cancel" className="mr-3" />
              </div>
            ) : (
              <div onClick={() => onModalAction(1)}>
                <Chip type="chips_square_request" className="mr-3" />
              </div>
            )}

            <Button
              size="small"
              type="lightGray"
              className="w-[100px] mr-8 body-l-semibold"
              onClick={() => onModalAction(4)}
            >
              차단
            </Button>
            <div className="flex items-center cursor-pointer" onClick={onClose}>
              <IcX />
            </div>
          </div>
        </div>

        <div className="flex flex-row justify-between w-full px-6 py-[2px] bg-gradient-to-r from-violet-50 to-green-50">
          <div className="flex flex-row items-center gap-2 body-s-semibold text-text-base">
            <span>[시스템 알림]</span>
            <span>비자 발급이 완료되었다면 경험을 후기로 남겨주세요.</span>
          </div>
          <Chip type="chips_square_review" />
        </div>
      </div>
    </>
  );
};

export default ChatRoomHeader;
