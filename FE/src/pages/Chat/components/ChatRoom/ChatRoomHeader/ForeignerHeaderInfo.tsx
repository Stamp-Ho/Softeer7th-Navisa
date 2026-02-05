import { IcArrows } from "../../../../../assets/icon/StratisUi";
import { nationList } from "../../../../../constants/nations";
import type { ForeignerInfo } from "../../../../../types/chatRoomTypes";

type Props = {
  data: ForeignerInfo;
};

const ForeignerHeaderInfo = ({ data }: Props) => {
  return (
    <div className="flex flex-row items-center title-m-bold text-text-base">
      {data.basicInfo.nickname}
      <div className="pl-[1px] h-8 mx-4 bg-border-normal" />
      <div className="flex flex-col justify-between">
        <div className="flex flex-row items-center">
          <span className="mr-3 body-l-semibold">
            {data.expectedInfo.targetJob}
          </span>
          <span className="mr-1 caption-m-medium">
            {data.expectedInfo.startDate}
          </span>
          <span className="caption-m-medium">입사 예정</span>
        </div>
        <div className="flex flex-row gap-1 items-center caption-m-medium text-text-sub">
          {data.basicInfo.nationIdList.map((nationIdx) => (
            <span key={nationIdx}>{nationList[nationIdx]}</span>
          ))}
        </div>
      </div>
      <div
        className="flex items-center -rotate-90 cursor-pointer ml-6"
        onClick={() => alert(data.basicInfo.foreignerId)}
      >
        <IcArrows size={20} />
      </div>
    </div>
  );
};

export default ForeignerHeaderInfo;
