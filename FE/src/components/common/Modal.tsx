import { IcX } from "../../assets/icon/StratisUi";
import type { ModalProps } from "../../types/modalProps";

const Modal = ({ children, className, onClose }: ModalProps) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div
        className={`bg-white p-5 rounded-[20px] flex flex-col items-center w-124
        shadow`}
      >
        <div
          className="ml-auto flex justify-end cursor-pointer"
          onClick={onClose}
        >
          <IcX />
        </div>
        <div className={`w-full ${className}`}>{children}</div>
      </div>
    </div>
  );
};

export default Modal;
