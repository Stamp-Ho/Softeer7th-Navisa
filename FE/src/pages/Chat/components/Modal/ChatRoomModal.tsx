import Modal from "../../../../components/common/Modal";
import ChatModalBlock from "./ChatModalBlock";
import ChatModalCancel from "./ChatModalCancel";
import ChatModalProposal from "./ChatModalProposal";
import ChatModalReply from "./ChatModalReply";
import { useRef, useEffect } from "react";

type ChatRoomModalProps = {
  onModalAction: (num: number) => void;
  modalView: number;
  roomId: number;
};

const ChatRoomModal = ({
  onModalAction,
  modalView,
  roomId,
}: ChatRoomModalProps) => {
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

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key !== "Tab") return;

      const focusableElements = modal.querySelectorAll(
        'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
      );
      const firstElement = focusableElements[0] as HTMLElement;
      const lastElement = focusableElements[
        focusableElements.length - 1
      ] as HTMLElement;

      if (e.shiftKey) {
        if (document.activeElement === firstElement) {
          e.preventDefault();
          lastElement.focus();
        }
      } else {
        if (document.activeElement === lastElement) {
          e.preventDefault();
          firstElement.focus();
        }
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  return (
    <Modal
      ref={modalRef}
      className="flex flex-col items-center px-5 pt-4 pb-6.25"
      onClose={() => onModalAction(0)}
    >
      {modalView === 1 && (
        <ChatModalProposal onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 2 && (
        <ChatModalCancel onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 3 && (
        <ChatModalReply onAnswer={onModalAction} roomId={roomId} />
      )}
      {modalView === 4 && (
        <ChatModalBlock onAnswer={onModalAction} roomId={roomId} />
      )}
    </Modal>
  );
};

export default ChatRoomModal;
