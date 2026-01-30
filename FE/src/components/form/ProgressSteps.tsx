import React from "react";
import ProgressDot from "../../assets/icon/ProgressDot";
import ProgressLine from "../../assets/icon/ProgressLine";
import type { FormSection } from "../../types/formType";

const ProgressSteps = ({
  title,
  formData,
}: {
  title: string;
  formData: FormSection[];
}) => {
  const currentIdx = 2;
  return (
    <div className="drop-shadow-[0_0_7px_#6860A040] py-7 px-5 rounded-[20px] bg-white flex flex-col gap-8">
      <h3 className="title-s-bold">{title} 항목 현황</h3>
      <div className="gap-5 flex flex-col">
        {formData.map((section) => (
          <div className="flex flex-col">
            <h4 className="body-l-semibold mb-3">{section.name}</h4>
            {section.fields.map((field, index) => (
              <React.Fragment key={`progress_${index}`}>
                <div className="flex flex-row gap-3 items-center body-l-medium">
                  <ProgressDot
                    status={"inProgress"}
                    isFirst={index === 0}
                    isLast={index === section.fields.length - 1}
                    isEditing={index === currentIdx}
                  />
                  <a
                    className={
                      "done" !== "done" ? "text-primary" : "text-text-sub"
                    }
                  >
                    {field.label}
                  </a>
                </div>
                {index < section.fields.length - 1 && (
                  <div className="-my-0.5">
                    <ProgressLine />
                  </div>
                )}
              </React.Fragment>
            ))}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProgressSteps;
