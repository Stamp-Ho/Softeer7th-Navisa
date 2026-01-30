interface TagProps {
  as?: React.ElementType; // 사용할 HTML 태그 (기본값: div)
  children: React.ReactNode;
  type?: string;
  className?: string;
}
const Tag = ({
  as: Component = "div",
  children,
  type = "default",
  className = "",
}: TagProps) => {
  let style = "flex flex-row justify-center items-center ";
  switch (type) {
    case "small_fill":
      style +=
        "bg-violet-50 text-primary h-[25px] rounded-[20px] px-2 caption-l-medium";
      break;
    case "small_fill_violet_max":
      style +=
        "bg-violet-50-transpar text-primary h-[25px] rounded-[20px] px-2 caption-l-medium";
      break;
    case "small_fill_gray":
      style +=
        "bg-gray-100 text-text-base h-[25px] rounded-[20px] px-2 caption-l-medium";
      break;
    case "small_line":
      style +=
        "border border-primary h-[25px] rounded-[20px] px-2 caption-l-medium";
      break;

    case "small_fill_green_max":
      style +=
        "bg-green-50 text-green-800 h-[25px] rounded-[20px] px-2 caption-l-medium";
      break;

    case "large_violet_off":
      style +=
        "bg-violet-50-transpar text-violet-500 h-[44px] rounded-[20px] px-2 body-l-semibold";
      break;
    case "large_gray_off":
      style +=
        "bg-gray-100 text-text-base h-[44px] rounded-[8px] px-2 body-l-semibold";
  }
  return <Component className={`${style}  ${className}`}>{children}</Component>;
};
export default Tag;
