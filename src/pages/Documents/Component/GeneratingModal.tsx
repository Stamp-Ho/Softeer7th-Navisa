import { useEffect, useRef } from "react";
import Modal from "../../../components/common/Modal";

const GeneratingModal = () => {
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
      <div className="w-full flex flex-col items-center justify-center gap-2 py-10">PDF 생성중...</div>
    </Modal>
  );
};
export default GeneratingModal;
