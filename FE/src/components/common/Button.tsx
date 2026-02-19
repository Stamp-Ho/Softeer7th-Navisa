import type React from "react";

interface ButtonProps {
  variant?: "lightGray" | "primary" | "grayLine" | "gray" | "violetLine" | "brightViolet" | "skeleton";
  size?: "tiny" | "small" | "medium" | "large" | "giant";
  className?: string;
  children?: React.ReactNode;
  onClick?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  disabled?: boolean;
  type?: "button" | "submit" | "reset" | undefined;
}

const Button = ({ variant = "lightGray", size = "medium", className = "", children, onClick, disabled = false, type = "button" }: ButtonProps) => {
  const getButtonStyle = () => {
    if (disabled) return "bg-gray-200 text-white cursor-not-allowed";
    return (variant === "skeleton" ? "cursor-default " : "cursor-pointer ") + (styles[variant] || styles.lightGray);
  };

  return (
    <button
      disabled={disabled}
      onClick={!disabled ? onClick : undefined}
      className={`
        flex items-center justify-center transition-all
        ${getButtonStyle()} 
        ${sizes[size] || sizes.medium} 
        ${className}
      `}
      type={type}
    >
      {children}
    </button>
  );
};

export default Button;

const styles = {
  lightGray: "bg-gray-50 text-text-base outline outline-border-light",
  primary: "bg-button-primary-bg text-text-inverse",
  grayLine: "bg-transparent outline outline-border-normal",
  gray: "bg-gray-200 text-white ",
  violetLine: "bg-violet-50 text-primary outline outline-violet-500",
  brightViolet: "bg-violet-50 text-primary",
  skeleton: "bg-gray-100 ",
};

const sizes = {
  tiny: "h-10 rounded-md",
  small: "h-12 rounded-[6px] body-l-semibold",
  medium: "h-14 rounded-[8px] body-l-semibold",
  large: "h-14 rounded-[10px] title-m-semibold",
  giant: "h-20 rounded-[10px] title-l-semibold",
};
