import { useState, useRef, useEffect } from "react";
import { useTranslation } from "react-i18next";
import Button from "../../../components/common/Button";
import Modal from "../../../components/common/Modal";
import TextInput from "../../../components/common/TextInput";
import { useCreateNewChatMutation } from "../../../api/mutations/useCreateNewChat";
import { useQueryClient } from "@tanstack/react-query";

type ChatActivateProps = {
  onClose: () => void;
  onSendSuccess: () => void;
  opponentProfileId: string;
  isAgent: boolean;
};

const ChatActivateModal = ({ onClose, onSendSuccess, opponentProfileId, isAgent }: ChatActivateProps) => {
  const modalRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation(["pages"]);
  const queryClient = useQueryClient();
  const [firstMessage, setFirstMessage] = useState<string>("");
  const { mutate: createNewChat, isPending } = useCreateNewChatMutation();

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

  const handleSendMessage = () => {
    const defaultMessage = isAgent ? t("profile.chatModalWantToHelp") : t("profile.chatModalWantToConsult");

    const messageToSend = firstMessage.trim() === "" ? defaultMessage : firstMessage;

    createNewChat(
      {
        opponentProfileId,
        content: messageToSend,
        sendAt: new Date().toISOString(),
      },
      {
        onSuccess: () => {
          // 부모 API 다시 불러오기
          queryClient.invalidateQueries({
            queryKey: [isAgent ? "foreignerDetail" : "agentDetail", opponentProfileId],
          });

          onSendSuccess();
          onClose();
        },
        onError: (error) => {
          if (error.message.includes("400")) {
            queryClient.invalidateQueries({
              queryKey: [isAgent ? "foreignerDetail" : "agentDetail", opponentProfileId],
            });
            onSendSuccess();
          }
          onClose();
        },
      },
    );
  };
  return (
    <Modal ref={modalRef} className="flex flex-col items-center px-5 pt-4 pb-6.25" onClose={onClose}>
      <div className="title-l-semibold text-text-base">
        {t("profile.chatModalSendConfirm", {
          role: isAgent ? t("profile.chatModalClient") : t("profile.chatModalAttorney"),
        })}
      </div>
      <div className="body-l-medium text-text-base mt-3 mb-8.5">
        {t("profile.chatModalReplyNotice", {
          role: isAgent ? t("profile.chatModalClient") : t("profile.chatModalAttorney"),
        })}
      </div>
      <TextInput
        className="placeholder:text-text-base mb-5"
        placeholder={isAgent ? t("profile.chatModalWantToHelp") : t("profile.chatModalWantToConsult")}
        value={firstMessage}
        setValue={setFirstMessage}
        tabIndex={1}
      />
      <Button
        variant="primary"
        size="large"
        className="w-full"
        onClick={handleSendMessage}
        disabled={isPending}
        tabIndex={1}
      >
        {t("profile.sendMessage")}
      </Button>
    </Modal>
  );
};

export default ChatActivateModal;
