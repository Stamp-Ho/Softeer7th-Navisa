type AlarmBadgeParams = {
  as?: React.ElementType; // 사용할 HTML 태그 (기본값: div)
  children: React.ReactNode;
  type?: string;
  className?: string;
};

const AlarmBadge = ({
  as: Component = "div",
  children,
  type = "default",
  className = "",
}: AlarmBadgeParams) => {
  let style =
    "flex rounded-full w-[30px] h-[30px] justify-center items-center p-[10px] body-s-semibold ";

  switch (type) {
    case "alarm_violet":
      style += "bg-violet-500 text-white";
      break;
    case "alarm_gray":
      style += "bg-gray-300 text-white";
  }

  return <Component className={`${style}  ${className}`}>{children}</Component>;
};

export default AlarmBadge;
