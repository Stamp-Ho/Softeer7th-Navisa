import type React from "react";

const Button = ({
  type = "default",
  className = "",
  children,
  onClick = () => {},
  disabled = false,
}: {
  type?: string;
  className?: string;
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
}) => {
  const buttonStyle = disabled
    ? "bg-gray-200 text-white cursor-not-allowed"
    : type === "disabled"
      ? ""
      : "bg-button-primary-bg text-text-inverse cursor-pointer";
  return (
    <button
      disabled={disabled}
      onClick={onClick}
      className={`${className} ${buttonStyle} px-spacing-600 py-spacing-500 rounded-radius-400`}
    >
      {children}
    </button>
  );
};

export default Button;
