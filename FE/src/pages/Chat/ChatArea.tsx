import { useRef, useEffect } from "react";

interface ChatAreaProps {
  className?: string;
  placeholder?: string;
  value: string;
  setValue: (v: string) => void;
  onSend?: () => void;
}

const ChatArea = ({
  className = "",
  placeholder = "메시지를 입력하세요.",
  value,
  setValue,
  // onSend,
}: ChatAreaProps) => {
  const chatAreaRef = useRef<HTMLTextAreaElement>(null);

  // 입력에 따라 높이 자동 조절 (max 4줄)
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

  // Enter / Shift+Enter 로직
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter") {
      if (e.shiftKey) {
        // 줄바꿈 허용
        return;
      } else {
        e.preventDefault();

        // 공백만 있으면 전송 방지
        if (value.trim().length === 0) return;

        // onSend(); // 전송 실행
        setValue(""); // 입력창 초기화
      }
    }
  };

  return (
    <textarea
      ref={chatAreaRef}
      className={`focus:outline-gray-300 focus:outline-2 
        flex flex-row items-center scrollbar-hide
         pl-6 pr-12 py-3 w-[832px] bg-background-sub rounded-[24px]
         resize-none ${className}`}
      placeholder={placeholder}
      value={value}
      onChange={(e) => setValue(e.target.value)}
      onKeyDown={handleKeyDown}
      rows={1}
      style={{ overflowY: "auto", minHeight: "48px" }}
    />
  );
};

export default ChatArea;
