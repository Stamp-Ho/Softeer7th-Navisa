import type { FormSection } from "../../types/formType";
import CheckBox from "../common/CheckBox";
import AddButton from "../common/AddButton";
import Tag from "../common/Tag";
import InputRenderer from "./InputRenderer";
import React from "react";

const NavisaForm = ({ formData }: { formData: FormSection[] }) => {
  return (
    <div className="flex flex-col self-stretch">
      {formData.map((section) => (
        <div className="p-10 bg-gray-50 mt-6 rounded-[20px] gap-8 flex flex-col ">
          <h3 className="title-l-bold mb-5">{section.name}</h3>
          {section.fields.map((field, index0) => (
            <div className="flex flex-col" key={`formSection_${index0}`}>
              <div className="flex flex-row items-center">
                <div className="mb-4">
                  <h4 className="title-l-semibold flex flex-row gap-1 items-center">
                    {field.isOptional && <Tag type="small_fill">선택</Tag>}
                    {field.label}
                  </h4>
                  {field.description && (
                    <h5 className="title-s-medium text-text-sub">
                      {field.description}
                    </h5>
                  )}
                </div>
                {field.disableToggleDescription && (
                  <div className="ml-auto mr-3">
                    <CheckBox
                      value={false}
                      setValue={() => {}}
                      label={field.disableToggleDescription}
                    />
                  </div>
                )}
              </div>
              <div className="">
                {field.inputLines.map((inputLine, index1) => {
                  let addBtnAlreadyRendered = !inputLine.getMany;
                  return (
                    <div
                      className="grid grid-cols-3 items-end gap-x-3 gap-y-6"
                      key={`inputLine_${index1}`}
                    >
                      {inputLine.inputs.map((input, index) => {
                        let btnBeforeThisInput = <></>;
                        let btnAfterThisInput = <></>;
                        if (!addBtnAlreadyRendered) {
                          if (
                            inputLine.getMany &&
                            !inputLine.addButtonAtBelowLines &&
                            index === inputLine.inputs.length - 1
                          ) {
                            btnAfterThisInput = (
                              <AddButton
                                onClick={() => alert("추가!")}
                                className=""
                              />
                            );
                            addBtnAlreadyRendered = true;
                          } else if (
                            input.changeRow &&
                            inputLine.addButtonAtFirstLine
                          ) {
                            btnBeforeThisInput = (
                              <AddButton
                                onClick={() => alert("추가!")}
                                className=""
                              />
                            );
                            addBtnAlreadyRendered = true;
                          }
                        }
                        return (
                          <React.Fragment key={`inputLine_${index}`}>
                            {btnBeforeThisInput}
                            <div
                              className={`flex flex-col ${(input.inputType === "textArea" || input.inputType === "longText" || input.inputType === "image") && "col-span-full"} ${input.changeRow && "col-start-1"}`}
                            >
                              {input.inputDescription && (
                                <a className="body-m-regular text-gray-400 mb-1">
                                  {input.inputDescription}
                                  {input.englishDescription &&
                                    `/${input.englishDescription}`}
                                </a>
                              )}
                              <InputRenderer input={input} />
                            </div>
                            {input.disableToggleDescription && (
                              <CheckBox
                                value={false}
                                setValue={() => {}}
                                label={input.disableToggleDescription}
                              />
                            )}
                            {btnAfterThisInput}
                          </React.Fragment>
                        );
                      })}
                      {inputLine.getMany &&
                        !inputLine.addButtonAtFirstLine &&
                        inputLine.addButtonAtBelowLines && (
                          <div className="col-start-1">
                            <AddButton
                              onClick={() => alert("추가!")}
                              className=""
                            />
                          </div>
                        )}
                    </div>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
};

export default NavisaForm;
