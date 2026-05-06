type AlarmBadgeParams = {
  as?: React.ElementType; // 사용할 HTML 태그 (기본값: div)
  children: React.ReactNode;
  isActive: boolean;
  className?: string;
};

const AlarmBadge = ({
  as: Component = "div",
  children,
  isActive = false,
  className = "",
}: AlarmBadgeParams) => {
  const activeStyle = isActive
    ? "bg-violet-500 text-white"
    : "bg-gray-300 text-white";

  return (
    <Component
      className={`flex mr-2 rounded-full w-[30px] h-[30px] justify-center items-center body-s-semibold ";
        ${activeStyle} ${className}`}
    >
      {children}
    </Component>
  );
};

export default AlarmBadge;
