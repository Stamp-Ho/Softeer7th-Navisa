import type React from "react";
import { IcX } from "../../assets/icon/StratisUi";

const Modal = ({
  children,
  className,
  onClose,
}: {
  children: React.ReactNode;
  className?: string;
  onClose: () => void;
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div
        className={`bg-white p-5 rounded-[20px] flex flex-col items-center w-124
        drop-shadow-[0_0_7px_#6860A040]`}
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
