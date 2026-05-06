import { Link } from "react-router-dom";
import { IcArrows } from "../../../../../assets/icon/StratisUi";
import { useNationLabels } from "../../../../../hooks/useLocalizationLists";
import type { ForeignerInfo } from "../../../../../types/chatRoomTypes";
import Tag from "../../../../../components/common/Tag";

type Props = {
  data?: ForeignerInfo;
};

const ForeignerHeaderInfo = ({ data }: Props) => {
  const nationLabels = useNationLabels();
  if (!data) return <SkeletonUi />;
  const content = (
    <div className="flex flex-row items-center title-m-bold text-text-base">
      {data?.nickname}
      <div className="pl-px h-8 mx-4 bg-border-normal" />
      <div className="flex flex-col justify-between">
        <div className="flex flex-row items-center">
          <span className="mr-3 body-l-semibold">{data?.expectedJob}</span>
        </div>
        <div className="flex flex-row gap-1 items-center caption-m-medium text-text-sub">
          {data?.nationalityIds?.map((nationIdx) => (
            <span key={nationIdx}>{nationLabels[nationIdx]}</span>
          ))}
        </div>
      </div>
      <div className="flex items-center -rotate-90 cursor-pointer ml-6">
        <IcArrows size={20} />
      </div>
    </div>
  );
  return data?.foreignerId ? <Link to={`/profile/foreigner/${data.foreignerId}`}>{content}</Link> : content;
};

export default ForeignerHeaderInfo;

const SkeletonUi = () => {
  return (
    <div className="flex flex-row items-center title-m-bold text-text-base">
      <Tag variant="small_fill_gray" className="w-25 h-6" />
      <div className="pl-px h-8 mx-4 bg-border-normal" />
      <div className="flex flex-col justify-between gap-1">
        <div className="flex flex-row items-center gap-2">
          <Tag variant="tiny_skeleton" className="w-30 h-6" />
        </div>
        <Tag variant="tiny_skeleton" className="w-15 h-6" />
      </div>
      <div className="flex items-center -rotate-90 cursor-pointer ml-6">
        <IcArrows size={20} />
      </div>
    </div>
  );
};
