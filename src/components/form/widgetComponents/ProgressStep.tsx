import { useFormContext, useWatch } from "react-hook-form";
import ProgressDot from "../../../assets/icon/ProgressDot";
import ProgressLine from "../../../assets/icon/ProgressLine";
import { calculateOnlyInputs } from "../utils/formUtils";

const ProgressStep = ({
  label,
  sectionIndex,
  fieldIndex = -1,
  currentIndex,
  parentLength,
}: {
  label: string;
  sectionIndex: number;
  fieldIndex?: number;
  currentIndex: number;
  parentLength: number;
}) => {
  const { control } = useFormContext();
  const targetLabel = fieldIndex >= 0 ? `${sectionIndex}.sectionData.${fieldIndex}` : `${sectionIndex}.sectionData`;
  const targetData = useWatch({
    name: [targetLabel],
    control,
  });
  const { totalCount, filledCount } = calculateOnlyInputs(targetData);

  const status = filledCount === totalCount ? "done" : filledCount === 0 ? "empty" : "inProgress";

  const leafIndex = fieldIndex >= 0 ? fieldIndex : sectionIndex;
  return (
    <>
      <div className="flex flex-row gap-3 items-center body-l-medium cursor-pointer">
        <ProgressDot
          status={status}
          isFirst={leafIndex === 0}
          isLast={leafIndex === parentLength - 1}
          isEditing={leafIndex === currentIndex}
        />
        <a className={status === "done" ? "text-primary" : "text-text-sub"}>{label}</a>
      </div>
      {leafIndex < parentLength - 1 && (
        <div className="-my-0.5">
          <ProgressLine />
        </div>
      )}
    </>
  );
};

export default ProgressStep;
