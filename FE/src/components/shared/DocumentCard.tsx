import { Link } from "react-router-dom";
import { IcFile2 } from "../../assets/icon/StratisUi";
import Button from "../common/Button";
import Tag from "../common/Tag";
import type { RecentVisaFormsResponse } from "../../api/types/etc";

const DocumentCard = ({
  document = {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
}: {
  document?: RecentVisaFormsResponse;
}) => {
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      <div className="w-18.75 h-26.25 overflow-hidden rounded-xl flex outline outline-border-normal">
        <img
          src={document.foreignerProfileImgUrl}
          alt="https://placehold.co/76x106"
        />
      </div>
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag type="small_fill_violet_max">
            {document.isDone ? "작성완료" : "작성중"}
          </Tag>
          <Tag type="small_fill_green_max">{document.currentStep}/138칸</Tag>
          <div className="ml-auto caption-m-medium text-text-sub">
            최근 수정 · {document.lastModifiedAt}
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
