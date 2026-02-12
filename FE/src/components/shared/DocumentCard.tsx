import { Link } from "react-router-dom";
import { IcFile2 } from "../../assets/icon/StratisUi";
import Button from "../common/Button";
import Tag from "../common/Tag";
import type { RecentVisaFormsResponse } from "../../api/types/etc";
import { formatToLocalTime } from "../../utils/formatToLocalTime";

const DocumentCard = ({ document }: { document?: RecentVisaFormsResponse }) => {
  if (!document) return skeletonUI();
  const lastModifiedAtLocalTime = formatToLocalTime(document.lastModifiedAt);

  const formattedTime =
    lastModifiedAtLocalTime.slice(0, 12) +
    " · " +
    lastModifiedAtLocalTime.slice(14, 19);
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      {document.foreignerProfileImgUrl ? (
        <div className="w-18.75 h-26.25 overflow-hidden rounded-xl flex outline outline-border-normal">
          <img
            src={
              "https://cloudfront.navisa.site/" +
              document.foreignerProfileImgUrl
            }
            alt="외국인 프로필 사진"
          />
        </div>
      ) : (
        <div className="w-18.75 h-26.25 rounded-xl bg-gray-100" />
      )}
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag type="small_fill_violet_max">
            {document.isDone ? "작성완료" : "작성중"}
          </Tag>
          <Tag type="small_fill_green_max">{document.currentStep}/138칸</Tag>
          <div className="ml-auto caption-m-medium text-text-sub">
            최근 수정 · {formattedTime}
          </div>
        </div>
        <h4 className="title-m-bold mt-3">{document.title}</h4>
        <div className="ml-auto flex flex-row gap-3">
          <Button
            type="grayLine"
            size="tiny"
            className="w-10 flex items-center justify-center relative font-[pretendard] text-[8px]"
          >
            <div className="absolute">
              <IcFile2 />
            </div>
            pdf
          </Button>
          <Link to={`/document/${document.applicationFormId}`}>
            <Button
              type={document.isDone ? "grayLine" : "primary"}
              size="tiny"
              className="w-30"
            >
              {document.isDone ? "문서 활성화" : "작성하기"}
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default DocumentCard;

const skeletonUI = () => {
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      <div className="w-18.75 h-26.25 rounded-xl bg-gray-100" />
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag type="small_fill_gray" className="w-12" />
          <Tag type="small_fill_gray" className="w-14" />
          <Tag type="small_fill_gray" className="w-30 ml-auto" />
        </div>
        <Tag type="small_fill_gray" className="w-35 mt-3 mb-1" />
        <div className="ml-auto flex flex-row gap-3">
          <Button type="skeleton" size="tiny" className="w-10" />
          <Button type="skeleton" size="tiny" className="w-30" />
        </div>
      </div>
    </div>
  );
};
