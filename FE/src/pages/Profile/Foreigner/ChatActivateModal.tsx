import { useState } from "react";
import { useTranslation } from "react-i18next";
import Button from "../../../components/common/Button";
import Modal from "../../../components/common/Modal";
import TextInput from "../../../components/common/TextInput";
import { useCreateNewChat } from "../../../api/mutations/useCreateNewChat";
import { useQueryClient } from "@tanstack/react-query";

type ChatActivateProps = {
  onClose: () => void;
  onSendSuccess: () => void;
  opponentProfileId: string;
  isAgent: boolean;
};

const ChatActivateModal = ({ onClose, onSendSuccess, opponentProfileId, isAgent }: ChatActivateProps) => {
  const { t } = useTranslation(["pages"]);
  const queryClient = useQueryClient();
  const [firstMessage, setFirstMessage] = useState<string>("");
  const { mutate: createNewChat, isPending } = useCreateNewChat();

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
      },
    );
  };
  return (
    <Modal className="flex flex-col items-center px-5 pt-4 pb-6.25" onClose={onClose}>
      <div className="title-l-semibold text-text-base">{t("profile.chatModalSendConfirm", { role: isAgent ? t("profile.chatModalClient") : t("profile.chatModalAttorney") })}</div>
      <div className="body-l-medium text-text-base mt-3 mb-8.5">{t("profile.chatModalReplyNotice", { role: isAgent ? t("profile.chatModalClient") : t("profile.chatModalAttorney") })}</div>
      <TextInput
        className="placeholder:text-text-base mb-5"
        placeholder={isAgent ? t("profile.chatModalWantToHelp") : t("profile.chatModalWantToConsult")}
        value={firstMessage}
        setValue={setFirstMessage}
      />
      <Button variant="primary" size="large" className="w-full" onClick={handleSendMessage} disabled={isPending}>
        {t("profile.sendMessage")}
      </Button>
    </Modal>
  );
};

export default ChatActivateModal;
