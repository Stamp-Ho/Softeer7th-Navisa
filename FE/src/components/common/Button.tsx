import type React from "react";

type buttonType = "lightGray" | "primary" | "grayLine";
const Button = ({
  type = "lightGray",
  size = "medium",
  className = "",
  children,
  onClick = () => {},
  disabled = false,
}: {
  type?: string;
  size?: string;
  className?: string;
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
}) => {
  const buttonStyle = disabled
    ? "bg-gray-200 text-white cursor-not-allowed"
    : type === "lightGray"
      ? "bg-gray-50 text-text-base outline outline-border-light"
      : type === "primary"
        ? "bg-button-primary-bg text-text-inverse "
        : type === "grayLine"
          ? "bg-transparent outline outline-border-normal"
          : "bg-violet-50 text-primary outline outline-violet-500 ";

  const buttonSize =
    size === "small"
      ? "h-12 rounded-[6px] body-l-semibold"
      : size === "medium"
        ? "h-14 rounded-[8px] body-l-semibold"
        : size === "large"
          ? "h-14 rounded-[10px] title-m-semibold"
          : "h-20 rounded-[10px] title-l-semibold";
  return (
    <button
      disabled={disabled}
      onClick={onClick}
      className={`cursor-pointer ${buttonStyle} ${buttonSize} ${className} `}
    >
      {children}
    </button>
  );
};

export default Button;
