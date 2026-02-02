import { Link } from "react-router-dom";
import { IcFile2 } from "../../assets/icon/StratisUi";
import type { documentType } from "../../pages/Documents/Documents";
import Button from "../common/Button";
import Tag from "../common/Tag";

const DocumentCard = ({
  document,
  documentId = 0,
}: {
  document: documentType;
  documentId: number;
}) => {
  return (
    <div className="flex flex-row w-full h-fit p-4 gap-3 bg-white rounded-[10px] shadow">
      <div className="w-18.75 h-26.25 overflow-hidden rounded-xl flex outline outline-border-normal">
        <img
          src={"https://placehold.co/76x106"}
          alt="https://placehold.co/76x106"
        />
      </div>
      <div className="flex flex-col flex-1">
        <div className="flex flex-row gap-1.5">
          <Tag type="small_fill_violet_max">
            {document.editing ? "작성중" : "작성완료"}
          </Tag>
          <Tag type="small_fill_green_max">{document.filledFields}/138칸</Tag>
          <div className="ml-auto caption-m-medium text-text-sub">
            최근 수정 · {document.lastEdittedAt}
          </div>
        </div>
        <h4 className="title-m-bold mt-3">{document.name}</h4>
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
          <Link to={`/document/${documentId}`}>
            <Button
              type={document.editing ? "primary" : "grayLine"}
              size="tiny"
              className="w-30"
            >
              {document.editing ? "작성하기" : "문서 활성화"}
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default DocumentCard;
