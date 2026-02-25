import { useEffect, useRef } from "react";
import Button from "../../../components/common/Button";
import Modal from "../../../components/common/Modal";
import { useTranslation } from "react-i18next";

const ConfirmToExportModal = ({ onConfirm, onCancel }: { onConfirm: () => void; onCancel: () => void }) => {
  const { t } = useTranslation(["pages"]);
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const modal = modalRef.current;
    if (!modal) return;

    // 모달이 마운트될 때 첫 번째 포커스 가능한 요소로 포커스 이동
    const focusableElements = modal.querySelectorAll(
      'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
    );
    const firstElement = focusableElements[0] as HTMLElement;
    if (firstElement) {
      firstElement.focus();
    }
  }, []);
  return (
    <Modal ref={modalRef}>
      <div className="w-full flex flex-col items-center justify-center gap-2">
        <div className="title-l-medium mt-3 mb-1">{t("documents.confirmExport")}</div>
        <div className="body-l-medium -mb-1 text-primary">{t("documents.exportInfo1")}</div>
        <div className="body-l-medium -mb-1 text-primary">{t("documents.exportInfo2")}</div>
        <div className="body-l-medium mb-3 text-primary">{t("documents.exportInfo3")}</div>
        <div className="flex flex-row w-full gap-3">
          <Button variant="lightGray" onClick={onCancel} className="flex-1" tabIndex={1}>
            {t("documents.cancel")}
          </Button>
          <Button variant="primary" onClick={onConfirm} className="flex-1" tabIndex={1}>
            {t("documents.exportPdf")}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
export default ConfirmToExportModal;
