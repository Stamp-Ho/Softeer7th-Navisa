interface TagProps {
  as?: React.ElementType; // 사용할 HTML 태그 (기본값: div)
  children: React.ReactNode;
  type?: string;
}
const Tag = ({
  as: Component = "div",
  children,
  type = "default",
  ...props
}: TagProps) => {
  let style =
    "flex flex-row justify-center items-center h-[25px] rounded-[20px] w-fit px-2 caption-l-medium ";
  switch (type) {
    case "small_fill":
      style += "bg-violet-50 text-primary";
      break;
    case "small_fill_violet_max":
      style += "bg-violet-50-transpar text-primary";
      break;
    case "small_fill_gray":
      style += "bg-gray-100 text-text-base";
      break;
    case "small_line":
      style += "border border-primary";
  }
  return <Component className={style}>{children}</Component>;
};
export default Tag;
