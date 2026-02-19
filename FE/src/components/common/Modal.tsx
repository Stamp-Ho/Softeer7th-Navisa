import { IcX } from "../../assets/icon/StratisUi";
import type { ModalProps } from "../../types/modalProps";
import { useTranslation } from "react-i18next";

const Modal = ({ children, className, onClose }: ModalProps) => {
  const { t } = useTranslation(["common"]);
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div
        className={`bg-white p-5 rounded-[20px] flex flex-col items-center w-124
        shadow`}
      >
        {onClose && (
          <div className="ml-auto flex justify-end cursor-pointer" onClick={onClose} title={t("button.close")}>
            <IcX />
          </div>
        )}

        <div className={`w-full ${className}`}>{children}</div>
      </div>
    </div>
  );
};

export default Modal;
