import { useParams } from "react-router-dom";
import NavisaForm from "../../../components/form/NavisaForm";
import Button from "../../../components/common/Button";
import ProgressStepWidget from "../../../components/form/ProgressStepWidget";
import {
  IcArrowUp,
  IcDot,
  IcDownload,
  IcFile2,
  IcGraduationThick,
  IcLuggageThick,
  IcMessage,
  IcPassport,
  IcPlus,
} from "../../../assets/icon/StratisUi";
import { useState } from "react";
import { useDocumentScroll } from "./hooks/useDocumentScroll";
import { editDocumentData } from "./constants";

const informationMessage = [
  "신청인은 사실에 근거하여 빠짐없이 정확하게 신청서를 작성하여야 합니다.",
  "신청서상의 모든 질문에 대한 답변은 한글 또는 영문으로 기재하여야 합니다.",
  "선택사항은 해당 칸을 선택하시길 바랍니다.",
  "‘기타'를 선택한 경우, 상세내용을 기재하시기 바랍니다.",
];
const EditDocument = () => {
  const { documentId } = useParams<{ documentId: string }>();

  const {
    scrollRef,
    handleScroll,
    goToSection,
    goTop,
    currentSectionIndex,
    getMaskStyle,
  } = useDocumentScroll();

  const [addedPassport, setAddedPassport] = useState(false);
  const [addedDegree, setAddedDegree] = useState(false);
  const [addedResume, setAddedResume] = useState(false);

  const elementBeforeSteps = (
    <>
      <div>
        <h4>서류 파일 업로드 {documentId}</h4>
        <h5>
          서류를 업로드하면, AI가 서류에 포함된 정보를 추출해 신청서 항목의
          빈칸을 자동으로 채워줘요.
        </h5>
      </div>
      <div className="flex flex-col gap-3">
        <Button
          type={addedPassport ? "brightViolet" : "grayLine"}
          size="small"
          className="flex justify-start items-center px-4 gap-1.5"
          onClick={() => setAddedPassport((prev) => !prev)}
        >
          <IcPassport isActive={addedPassport} />
          <div className="mr-auto">
            {addedPassport ? (
              <div className="flex flex-col items-start">
                <h5 className="caption-s-regular text-gray-400">여권 사본</h5>
                <a className="body-l-semiboid -mt-0.5">{"여권사본.png"}</a>
              </div>
            ) : (
              <>여권 사본 추가하기</>
            )}
          </div>
          <IcPlus color="black" />
        </Button>
        <Button
          type={addedDegree ? "brightViolet" : "grayLine"}
          size="small"
          className="flex justify-start items-center px-4 gap-1.5"
          onClick={() => setAddedDegree((prev) => !prev)}
        >
          <IcGraduationThick isActive={addedDegree} />
          <div className="mr-auto">
            {addedDegree ? (
              <div className="flex flex-col items-start">
                <h5 className="caption-s-regular text-gray-400">학위 증명서</h5>
                <a className="body-l-semiboid -mt-0.5">{"학위증명서.png"}</a>
              </div>
            ) : (
              <>학위 증명서 추가하기</>
            )}
          </div>
          <IcPlus color="black" />
        </Button>
        <Button
          type={addedResume ? "brightViolet" : "grayLine"}
          size="small"
          className="flex justify-start items-center px-4 gap-1.5"
          onClick={() => setAddedResume((prev) => !prev)}
        >
          <IcLuggageThick isActive={addedResume} />
          <div className="mr-auto">
            {addedResume ? (
              <div className="flex flex-col items-start">
                <h5 className="caption-s-regular text-gray-400">이력서</h5>
                <a className="body-l-semiboid -mt-0.5">{"이력서.png"}</a>
              </div>
            ) : (
              <>이력서 추가하기</>
            )}
          </div>
          <IcPlus color="black" />
        </Button>
      </div>
      <div className="w-full pt-px -my-1 bg-border-normal" />
    </>
  );
  const elementAfterSteps = (
    <>
      <div className="w-full pt-px -mb-1 bg-border-normal" />
      <div className="grid grid-cols-2 gap-3">
        <Button
          type="grayLine"
          className="flex items-center justify-center gap-2"
        >
          <IcFile2 /> PDF 미리보기
        </Button>
        <Button
          type="grayLine"
          className="flex items-center justify-center gap-2"
        >
          <IcDownload /> PDF 다운로드
        </Button>
      </div>
    </>
  );

  return (
    <div className="flex flex-row overflow-y-auto w-fit">
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className={`w-284 overflow-auto scrollbar-hide ${getMaskStyle()}`}
        style={{ height: "calc(100vh - 100px)" }}
      >
        <div className="flex flex-col pb-15 pt-2.5">
          <h2 className="headline-m-bold text-text-base mb-3">
            비자 신청서 작성 (사증발급인정신청서)
          </h2>
          <a className="body-l-medium text-text-base mb-5">
            직접 빈칸에 입력하거나 '서류 파일 업로드'를 통해 AI의 도움을
            받아보세요.
          </a>
          <ul className="flex flex-col bg-green-bright body-l-medium text-green-vivid gap-1.5 rounded-[20px] py-7 px-5.25">
            {informationMessage.map((text, idx) => (
              <li
                className="flex flex-row items-center gap-0.5"
                key={`inform_${idx}`}
              >
                <IcDot size={16} color="var(--green-vivid)" />
                {text}
              </li>
            ))}
          </ul>
          <NavisaForm
            formData={editDocumentData}
            addIndex={true}
            startsWithImage={true}
          />
        </div>
      </div>
      <div className="w-fit ml-4 left-0 mt-7 flex flex-row">
        <div className="flex flex-col w-92 gap-5 ">
          <Button type="primary" className="drop-shadow-[0_0_7px_#6860A040]">
            저장
          </Button>
          <ProgressStepWidget
            title="신청서"
            formData={editDocumentData}
            currentSectionId={currentSectionIndex}
            onSectionClick={goToSection}
            stepBySection={true}
            elementBeforeSteps={elementBeforeSteps}
            elementAfterSteps={elementAfterSteps}
          />
        </div>
        <div className="flex flex-col self-end">
          <button
            className="m-4 mt-auto rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-white w-16 h-16 flex items-center justify-center"
            onClick={goTop}
          >
            <IcArrowUp size={20} />
          </button>
          <button
            className="m-4 mt-auto rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-black w-16 h-16 flex items-center justify-center"
            onClick={() => {}}
          >
            <IcMessage color="white" />
          </button>
        </div>
      </div>
    </div>
  );
};
export default EditDocument;
