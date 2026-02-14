import { useRef, useEffect } from "react";
import { useChatSender } from "../../../../../api/websocket/useChatSender";

interface ChatAreaProps {
  className?: string;
  placeholder?: string;
  value: string;
  setValue: (v: string) => void;
  roomId: number;
}

const ChatArea = ({
  className = "",
  placeholder = "메시지를 입력하세요.",
  value,
  setValue,
  roomId,
}: ChatAreaProps) => {
  const chatAreaRef = useRef<HTMLTextAreaElement>(null);
  const isComposing = useRef(false);
  const { sendChat } = useChatSender();

  // 높이 자동 조절
  const adjustHeight = () => {
    const ta = chatAreaRef.current;
    if (!ta) return;

    ta.style.height = "auto";
    const lineHeight = parseInt(getComputedStyle(ta).lineHeight || "20", 10);
    const maxHeight = lineHeight * 4;

    ta.style.height = `${Math.min(ta.scrollHeight, maxHeight)}px`;
  };

  useEffect(() => {
    adjustHeight();
  }, [value]);

  // Enter / Shift+Enter
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (isComposing.current) return; // 한글 입력 중이면 무시

    if (e.key === "Enter") {
      if (e.shiftKey) return; // 줄바꿈 허용

      e.preventDefault();

      if (value.trim().length === 0) return; // 공백만 있으면 전송 방지

      sendChat(roomId, "TEXT", value);
      setValue(""); // 입력창 초기화
    }
  };

  return (
    <textarea
      ref={chatAreaRef}
      className={`focus:outline-gray-300 focus:outline-2 
        flex flex-row items-center scrollbar-hide
         pl-6 pr-12 py-3 w-208 bg-background-sub rounded-6
         resize-none ${className}`}
      placeholder={placeholder}
      value={value}
      onChange={(e) => setValue(e.target.value)}
      onCompositionStart={() => (isComposing.current = true)}
      onCompositionEnd={() => (isComposing.current = false)}
      onKeyDown={handleKeyDown}
      rows={1}
      style={{ overflowY: "auto", minHeight: "48px" }}
    />
  );
};

export default ChatArea;
