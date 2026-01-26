type ToolTipMessageProps = {
  message: string;
  className?: string;
};

const ToolTipMessage = ({ message }: ToolTipMessageProps) => {
  return (
    <div className="pb-3">
      <div
        className={`
        relative inline-flex items-center justify-center
        px-5 py-3
        bg-white
        rounded-radius-700
        text-violet-500
        caption-l-medium
        text-center
        drop-shadow-[0_0_3px_rgba(104,96,160,0.25)]
      `}
      >
        {message}
        <div
          className="
          absolute -bottom-2 right-6
          w-4 h-4
          bg-white
          rotate-45
          rounded-br-radius-200
        "
        />
      </div>
    </div>
  );
};

export default ToolTipMessage;
