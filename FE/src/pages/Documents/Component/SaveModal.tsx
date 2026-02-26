import { useRef } from "react";
import Modal from "../../../components/common/Modal";

const SaveModal = () => {
  const modalRef = useRef<HTMLDivElement>(null);

  return (
    <Modal ref={modalRef}>
      <div className="w-full flex flex-col items-center justify-center gap-2 py-10">저장 중...</div>
    </Modal>
  );
};
export default SaveModal;
