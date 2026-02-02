import ProgressDot from "../../assets/icon/ProgressDot";
import ProgressLine from "../../assets/icon/ProgressLine";

const ProgressStep = ({
  label,
  index,
  currentIndex,
  parentLength,
}: {
  label: string;
  index: number;
  currentIndex: number;
  parentLength: number;
}) => {
  return (
    <>
      <div className="flex flex-row gap-3 items-center body-l-medium cursor-pointer">
        <ProgressDot
          status={"inProgress"}
          isFirst={index === 0}
          isLast={index === parentLength - 1}
          isEditing={index === currentIndex}
        />
        <a className={"done" !== "done" ? "text-primary" : "text-text-sub"}>
          {label}
        </a>
      </div>
      {index < parentLength - 1 && (
        <div className="-my-0.5">
          <ProgressLine />
        </div>
      )}
    </>
  );
};

export default ProgressStep;
