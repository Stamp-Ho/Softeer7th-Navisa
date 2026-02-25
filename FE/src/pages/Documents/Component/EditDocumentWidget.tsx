import { useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { IcDownload, IcFile2, IcMessage } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import GoTopFloating from "../../../components/common/GoTopFloating";
import ProgressStepWidget from "../../../components/form/widgetComponents/ProgressStepWidget";
import type { FormSection } from "../../../types/formType";
import { useEffect, useState } from "react";
import FloatingChatModal from "./FloatingChatModal";
import { useGeneratePdf } from "../hooks/useGeneratePdf";
import ConfirmToExportModal from "./ConfirmToExportModal";
import { usePatchFormStatusMutation } from "../../../api/mutations/usePatchFormStatusMutation";
import { useNavigate } from "react-router-dom";
import GeneratingModal from "./GeneratingModal";
import { useAuth } from "../../../contexts/AuthContextProvider";

const EditDocumentWidget = ({
  editDocumentData,
  currentSectionIndex,
  isAgent,
  isDone,
  imageUrl,
  goToSection,
  goTop,
  documentId,
  chatRoomId,
  readOnly = false,
  loadedFromLocalStorage = false,
  setLoadedFromLocalStorage,
}: EditDocumentWidgetProps) => {
  const { t } = useTranslation(["pages"]);
  const filledFormData = useWatch();
  const navigate = useNavigate();
  const { userType } = useAuth();
  const { previewPdf, downloadPdf, generating } = useGeneratePdf();
  const [confirmModalOn, setConfirmModalOn] = useState(false);
  const patchStatus = usePatchFormStatusMutation(() => setConfirmModalOn(false));
  const [isChatOpen, setIsChatOpen] = useState<boolean>(false);
  const [justLoaded, setJustLoaded] = useState(true);
  useEffect(() => {
    if (!readOnly) {
      const formValues = Object.values(filledFormData).slice(0, 9);
      const data = {
        updatedAt: new Date(),
        sections: formValues.map((section, i) => ({
          sectionId: i + 1,

          sectionData: section.sectionData.map((field: Record<string, any>) => ({
            ...field,
            values: field.values && Object.values(field.values),
          })),
        })),
      };
      window.localStorage.setItem(documentId, JSON.stringify(data));
      justLoaded || setLoadedFromLocalStorage(true);
    }
  }, [readOnly, filledFormData, documentId]);

  useEffect(() => {
    setTimeout(() => {
      setJustLoaded(false);
    }, 500);
  }, []);

  const handlePreviewPdf = () => previewPdf(filledFormData, imageUrl);
  const handleDownloadPdf = () => {
    if (isAgent && !isDone) {
      setConfirmModalOn(true);
    } else {
      downloadPdf(filledFormData, imageUrl);
    }
  };

  const onCancel = () => setConfirmModalOn(false);
  const onConfirm = () => {
    downloadPdf(filledFormData, imageUrl);
    patchStatus.mutate({ formId: documentId, isDone: true });
    navigate(-1);
  };
  const elementAfterSteps = (
    <>
      <div className="w-full pt-px -mb-1 bg-border-normal" />
      <div className="grid grid-cols-2 gap-3">
        <Button variant="grayLine" className="flex items-center justify-center gap-2" onClick={handlePreviewPdf}>
          <IcFile2 /> {t("documents.previewPdf")}
        </Button>
        <Button variant="grayLine" className="flex items-center justify-center gap-2" onClick={handleDownloadPdf}>
          <IcDownload />{" "}
          {isDone || userType.includes("FOREIGNER") ? t("documents.downloadPdf") : t("documents.exportPdf")}
        </Button>
      </div>
    </>
  );
  return (
    <div className="w-fit ml-4 left-0 mt-17 flex flex-row relative">
      {confirmModalOn && <ConfirmToExportModal onCancel={onCancel} onConfirm={onConfirm} />}
      {generating && <GeneratingModal />}
      <div className="flex flex-col w-92 gap-5 ">
        {loadedFromLocalStorage && (
          <span className="absolute h-3 w-92 caption-l-medium text-green-500 animate-pulse -mt-6 text-center">
            {t("documents.temporarySaveInfo")}
          </span>
        )}
        <Button variant="primary" className="drop-shadow-[0_0_7px_#6860A040]" type="submit">
          {t("documents.save")}
        </Button>
        <ProgressStepWidget
          title={t("documents.applicationForm")}
          formData={editDocumentData}
          currentSectionId={currentSectionIndex}
          onSectionClick={goToSection}
          stepBySection={true}
          elementAfterSteps={elementAfterSteps}
        />
      </div>
      <div className="relative flex flex-row self-end">
        {isChatOpen && chatRoomId !== null && (
          <FloatingChatModal onClose={() => setIsChatOpen(!isChatOpen)} chatRoomId={chatRoomId} />
        )}
        <div className="flex flex-col">
          <GoTopFloating onClick={goTop} className="m-4 mt-auto" />
          {chatRoomId !== null && (
            <button
              type="button"
              className="m-4 mt-auto rounded-full cursor-pointer drop-shadow-[0_0_7px_#6860A040] bg-black w-16 h-16 pb-0.5 flex items-center justify-center"
              onClick={() => setIsChatOpen(!isChatOpen)}
            >
              <IcMessage color="white" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default EditDocumentWidget;

type EditDocumentWidgetProps = {
  editDocumentData: FormSection[];
  currentSectionIndex: number;
  isAgent: boolean;
  isDone: boolean;
  imageUrl: string;
  goToSection: (i: number) => void;
  goTop: () => void;
  documentId: string;
  chatRoomId: number | null;
  readOnly: boolean;
  loadedFromLocalStorage: boolean;
  setLoadedFromLocalStorage: (value: boolean) => void;
};
