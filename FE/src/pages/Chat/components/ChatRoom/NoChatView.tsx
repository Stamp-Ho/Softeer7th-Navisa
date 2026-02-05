import { useContext } from "react";
import { IcArrows } from "../../../../assets/icon/StratisUi";
import { AuthContext } from "../../../../contexts/AuthContext";

type NoChatViewParams = {
  isFileReady: boolean;
};

const NoChatView = ({ isFileReady }: NoChatViewParams) => {
  const context = useContext(AuthContext);
  if (!context) return null;
  const { userType } = context;
  const isAgent = userType === "VALID_AGENT";

  return (
    <div className="flex flex-col mt-12 h-[904px]">
      <div className="headline-m-bold text-gray-1000 mb-13">상담 메시지</div>
      <div className="flex flex-col gap-4 justify-center items-center h-full">
        <div className="headline-s-medium text-gray-500">
          진행 중인 상담이 없어요.
        </div>
        <button className="flex flex-row items-center pl-5 title-m-semibold text-violet-500 cursor-pointer">
          {isFileReady
            ? isAgent
              ? "의뢰인 탐색하기"
              : "행정사 탐색하기"
            : "내 요건 등록하고 상담하기"}
          <span className="-rotate-90">
            <IcArrows stroke="var(--violet-500)" size={32} />
          </span>
        </button>
      </div>
    </div>
  );
};

export default NoChatView;
