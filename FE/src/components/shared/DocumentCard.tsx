import { Link } from "react-router-dom";
import { IcFile2 } from "../../assets/icon/StratisUi";
import Button from "../common/Button";
import Tag from "../common/Tag";
import type { RecentVisaFormsResponse } from "../../api/types/etc";

const DocumentCard = ({
  form = {
    applicationFormId: "abc",
    title: "주디",
    isDone: false,
    currentStep: 24,
    foreignerProfileImgUrl: "https://placehold.co/76x106",
    lastModifiedAt: "2025. 06. 21",
  },
}: {
  form?: RecentVisaFormsResponse;
}) => {
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      <div className="w-18.75 h-26.25 overflow-hidden rounded-xl flex outline outline-border-normal">
        <img
          src={form.foreignerProfileImgUrl}
          alt="https://placehold.co/76x106"
        />
      </div>
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag type="small_fill_violet_max">
            {form.isDone ? "작성완료" : "작성중"}
          </Tag>
          <Tag type="small_fill_green_max">{form.currentStep}/138칸</Tag>
          <div className="ml-auto caption-m-medium text-text-sub">
            최근 수정 · {form.lastModifiedAt}
          </div>
        </div>
        <h4 className="title-m-bold mt-3">{form.title}</h4>
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
          <Link to={`/document/${form.applicationFormId}`}>
            <Button
              type={form.isDone ? "grayLine" : "primary"}
              size="tiny"
              className="w-30"
            >
              {form.isDone ? "문서 활성화" : "작성하기"}
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default DocumentCard;
