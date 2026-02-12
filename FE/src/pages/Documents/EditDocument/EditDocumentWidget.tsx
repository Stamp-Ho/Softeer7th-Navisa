import { useWatch } from "react-hook-form";
import { IcDownload, IcFile2, IcMessage } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import GoTopFloating from "../../../components/common/GoTopFloating";
import ProgressStepWidget from "../../../components/form/ProgressStepWidget";
import type { FormSection } from "../../../types/formType";
import { useEffect, useState } from "react";

const EditDocumentWidget = ({
  editDocumentData,
  currentSectionIndex,
  goToSection,
  goTop,
  documentId,
}: {
  editDocumentData: FormSection[];
  currentSectionIndex: number;
  goToSection: (i: number) => void;
  goTop: () => void;
  documentId: string;
}) => {
  const filledFormData = useWatch();

  const [justRendered, setJustRendered] = useState(true);
  useEffect(() => {
    if (justRendered) return;
    const data = { updatedAt: new Date(), sections: filledFormData };
    window.localStorage.setItem(documentId, JSON.stringify(data));
  }, [filledFormData, documentId, justRendered]);

  useEffect(() => {
    setJustRendered(false);
  }, []);
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
    <div className="w-fit ml-4 left-0 mt-17 flex flex-row">
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
          elementAfterSteps={elementAfterSteps}
        />
      </div>
      <div className="flex flex-col self-end">
        <GoTopFloating onClick={goTop} className="m-4 mt-auto" />
        <button
          className="m-4 mt-auto rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-black w-16 h-16 pb-0.5 flex items-center justify-center"
          onClick={() => {}}
        >
          <IcMessage color="white" />
        </button>
      </div>
    </div>
  );
};

export default EditDocumentWidget;
