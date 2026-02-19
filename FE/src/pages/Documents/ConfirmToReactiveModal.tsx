import Button from "../../components/common/Button";
import Modal from "../../components/common/Modal";
import { useTranslation } from "react-i18next";

const ConfirmToReactiveModal = ({ onConfirm, onCancel }: { onConfirm: () => void; onCancel: () => void }) => {
  const { t } = useTranslation(["pages", "common"]);

  return (
    <Modal>
      <div className="w-full flex flex-col items-center justify-center gap-2">
        <div className="title-l-medium mt-3 mb-1">{t("documents.confirmModify")}</div>
        <div className="body-l-medium mb-3 text-primary">{t("documents.alreadyExported")}</div>
        <div className="flex flex-row w-full gap-3">
          <Button variant="lightGray" onClick={onCancel} className="flex-1">
            {t("button.cancel")}
          </Button>
          <Button variant="primary" onClick={onConfirm} className="flex-1">
            {t("documents.modifyDocument")}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
export default ConfirmToReactiveModal;
