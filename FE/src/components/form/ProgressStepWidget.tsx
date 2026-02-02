import React from "react";
import type { FormSection } from "../../types/formType";
import ProgressStep from "./ProgressStep";
import Tag from "../common/Tag";

const ProgressStepWidget = ({
  title,
  formData,
  stepBySection = false,
  elementBeforeSteps,
  elementAfterSteps,
}: {
  title: string;
  formData: FormSection[];
  stepBySection?: boolean;
  elementBeforeSteps?: React.ReactNode;
  elementAfterSteps?: React.ReactNode;
}) => {
  const currentIdx = 2;
  return (
    <div className="drop-shadow-[0_0_7px_#6860A040] py-7 px-5 rounded-[20px] bg-white flex flex-col gap-5">
      {elementBeforeSteps}
      <h3 className="title-s-bold flex flex-row gap-2">
        {title} 항목 현황
        {stepBySection && <Tag type="small_fill_gray">{105}/138칸</Tag>}
      </h3>
      {stepBySection ? (
        <div className="flex flex-col">
          {formData.map((section, index) => (
            <div className="flex flex-col">
              <ProgressStep
                label={section.name}
                index={index}
                currentIndex={currentIdx}
                parentLength={formData.length}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="gap-4 flex flex-col">
          {formData.map((section) => (
            <div className="flex flex-col">
              <h4 className="body-l-semibold mb-3">{section.name}</h4>
              {section.fields.map((field, index) => (
                <React.Fragment key={`progress_${index}`}>
                  <ProgressStep
                    label={field.label}
                    index={index}
                    currentIndex={currentIdx}
                    parentLength={section.fields.length}
                  />
                </React.Fragment>
              ))}
            </div>
          ))}
        </div>
      )}
      {elementAfterSteps}
    </div>
  );
};

export default ProgressStepWidget;
