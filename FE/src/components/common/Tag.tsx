import type React from "react";

const Tag = ({
  as: Component = "div",
  children,
  type = "default",
  className = "",
}: TagProps) => {
  // 2. 미리 정의된 스타일을 가져오고 없을 경우를 대비해 처리
  const typeStyle = TAG_STYLES[type] || TAG_STYLES.default;

  return (
    <Component className={`${BASE_STYLE} ${typeStyle} ${className}`}>
      {children}
    </Component>
  );
};

export default Tag;

type TagType =
  | "small_fill"
  | "small_fill_violet_max"
  | "small_fill_gray"
  | "small_fill_gray_dark"
  | "small_fill_gray_2"
  | "small_line"
  | "small_fill_green_max"
  | "large_violet_off"
  | "large_white_off"
  | "large_gray_off"
  | "default";

interface TagProps {
  as?: React.ElementType;
  children: React.ReactNode;
  type?: TagType;
  className?: string;
}

// 1. 공통 스타일과 타입별 스타일을 상수로 분리
const BASE_STYLE = "flex flex-row justify-center items-center";

const TAG_STYLES: Record<TagType, string> = {
  default: "",
  small_fill:
    "bg-violet-50 text-primary h-[25px] rounded-[20px] px-2 caption-l-medium",
  small_fill_violet_max:
    "bg-violet-50-transpar text-primary h-[25px] rounded-[20px] px-2 caption-l-medium",
  small_fill_gray:
    "bg-gray-100 text-text-base h-[25px] rounded-[20px] px-2 caption-l-medium",
  small_fill_gray_dark:
    "bg-gray-200 text-text-base h-[25px] rounded-[20px] px-2 caption-l-medium",
  small_fill_gray_2:
    "bg-gray-100 text-text-base h-[25px] px-2 caption-l-medium",
  small_line:
    "border border-primary h-[25px] rounded-[20px] px-2 caption-l-medium",
  small_fill_green_max:
    "bg-green-50 text-green-800 h-[25px] rounded-[20px] px-2 caption-l-medium",
  large_violet_off:
    "bg-violet-50-transpar text-violet-500 h-[44px] rounded-[20px] px-2 body-l-semibold",
  large_white_off: "bg-white h-[44px] rounded-[20px] px-2 body-l-semibold",
  large_gray_off:
    "bg-gray-100 text-text-base h-[44px] rounded-[8px] px-2 body-l-semibold",
};
