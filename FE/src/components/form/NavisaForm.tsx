import type { FormSection } from "../../types/formType";
import CheckBox from "../common/CheckBox";
import AddButton from "../common/AddButton";
import Tag from "../common/Tag";
import InputRenderer from "./InputRenderer";
import React, { useState } from "react";
import SubtractButton from "../common/SubtractButton";
import Radio from "../common/Radio";

const NavisaForm = ({
  formData,
  addIndex = false,
  startsWithImage = false,
}: {
  formData: FormSection[];
  addIndex?: boolean;
  startsWithImage?: boolean;
}) => {
  const [formStruct, setFormStruct] = useState(formData);

  // 줄 추가
  const handleAddField = (sIdx: number, fIdx: number) => {
    const subjectiveFieldIndex =
      startsWithImage && sIdx === 0 ? fIdx + 1 : fIdx;
    setFormStruct((prev) => {
      // 1. 전체 구조 깊은 복사 (중첩 구조이므로 중요!)
      const newStruct = JSON.parse(JSON.stringify(prev));

      const newLine = {
        ...newStruct[sIdx].fields[subjectiveFieldIndex].inputLines[0],
        rowId: Date.now(), // 고유 키 추가
      };
      newStruct[sIdx].fields[subjectiveFieldIndex].inputLines.push(newLine);
      return newStruct;
    });
  };

  // 줄 삭제
  const handleSubtractField = (
    sIdx: number,
    fIdx: number,
    inputLineIdx: number,
  ) => {
    const subjectiveFieldIndex =
      startsWithImage && sIdx === 0 ? fIdx + 1 : fIdx;
    setFormStruct((prev) => {
      const newStruct = JSON.parse(JSON.stringify(prev));
      const targetLines =
        newStruct[sIdx].fields[subjectiveFieldIndex].inputLines;

      // 최소 한 줄은 남기기
      if (targetLines.length <= 1) return prev;

      // 해당 인덱스 삭제
      targetLines.splice(inputLineIdx, 1);

      return newStruct;
    });
  };
  const colSpans = [
    "col-span-1",
    "col-span-2",
    "col-span-3",
    "col-span-4",
    "col-span-5",
    "col-span-6",
    "col-span-7",
    "col-span-8",
    "col-span-9",
  ];
  return (
    <div className="flex flex-col self-stretch mb-160">
      {formStruct.map((section, sectionIdx) => (
        <div
          className="p-10 bg-gray-50 mt-6 rounded-[20px] gap-8 flex flex-col "
          key={`form_section_${sectionIdx}`}
        >
          <h3 className="title-l-bold mb-5">
            {addIndex && `${sectionIdx + 1}. `}
            {section.name}
            {section.description && ` / ${section.description}`}
          </h3>
          {startsWithImage && sectionIdx === 0 && (
            <InputRenderer
              input={formStruct[0].fields[0].inputLines[0].inputs[0]}
            />
          )}
          {section.fields
            .slice(startsWithImage && sectionIdx === 0 ? 1 : 0)
            .map((field, fieldIdx) => (
              <div className="flex flex-col" key={`formSection_${fieldIdx}`}>
                <div className="flex flex-row items-center">
                  <div className="mb-4">
                    {addIndex && (
                      <Tag
                        type={
                          false
                            ? "small_fill_violet_max"
                            : "small_fill_gray_dark"
                        }
                        className="w-fit mb-2"
                      >
                        {sectionIdx + 1}-{fieldIdx + 1}
                      </Tag>
                    )}
                    <h4 className="title-l-semibold flex flex-row gap-1 items-center">
                      {field.isOptional && <Tag type="small_fill">선택</Tag>}
                      {field.label}
                    </h4>
                    {field.description && (
                      <h5 className="title-s-medium text-text-sub">
                        {field.description}
                      </h5>
                    )}
                    {field.canInputBlocked && (
                      <Radio
                        options={["예", "아니오"]}
                        className=" mt-3 w-56.5"
                      />
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
                <div className="flex flex-col gap-4">
                  {field.inputLines.map((inputLine, inputLineIdx) => {
                    let addBtnAlreadyRendered = !field.getMany;
                    return (
                      <div
                        className="grid grid-cols-9 items-end gap-x-3 gap-y-6"
                        key={inputLine.rowId || `field_${inputLineIdx}`}
                      >
                        {inputLineIdx > 0 && inputLine.inputs.length > 1 && (
                          <div className="col-span-9 mr-71.5 bg-gray-200 h-0.5 -mb-1.5" />
                        )}
                        {inputLine.inputs.map((input, inputIdx) => {
                          let btnBeforeThisInput = <></>;
                          let btnAfterThisInput = <></>;
                          if (!addBtnAlreadyRendered) {
                            if (
                              field.getMany &&
                              !field.addButtonAtBelowLines &&
                              inputLineIdx === 0 &&
                              inputIdx === inputLine.inputs.length - 1
                            ) {
                              btnAfterThisInput = (
                                <AddButton
                                  onClick={() =>
                                    handleAddField(sectionIdx, fieldIdx)
                                  }
                                />
                              );
                              addBtnAlreadyRendered = true;
                            } else if (
                              input.changeRow &&
                              field.addButtonAtFirstLine &&
                              inputLineIdx === 0
                            ) {
                              btnBeforeThisInput = (
                                <AddButton
                                  onClick={() =>
                                    handleAddField(sectionIdx, fieldIdx)
                                  }
                                />
                              );
                              addBtnAlreadyRendered = true;
                            }
                            if (inputLineIdx !== 0 && input.changeRow) {
                              btnBeforeThisInput = (
                                <SubtractButton
                                  onClick={() =>
                                    handleSubtractField(
                                      sectionIdx,
                                      fieldIdx,
                                      inputLineIdx,
                                    )
                                  }
                                />
                              );
                              addBtnAlreadyRendered = true;
                            } else if (
                              inputLineIdx !== 0 &&
                              inputLine.inputs.length === 1
                            ) {
                              btnAfterThisInput = (
                                <SubtractButton
                                  onClick={() =>
                                    handleSubtractField(
                                      sectionIdx,
                                      fieldIdx,
                                      inputLineIdx,
                                    )
                                  }
                                />
                              );
                            }
                          }
                          return (
                            <React.Fragment key={`field_${inputIdx}`}>
                              {btnBeforeThisInput}
                              <div
                                className={`flex flex-col ${input.colSpan ? colSpans[input.colSpan - 1] : "col-span-3"}
                                  ${input.changeRow ? "col-start-1" : ""}`}
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
                                  className="col-span-2"
                                />
                              )}
                              {btnAfterThisInput}
                            </React.Fragment>
                          );
                        })}
                        {field.getMany &&
                          !field.addButtonAtFirstLine &&
                          field.addButtonAtBelowLines &&
                          inputLineIdx === field.inputLines.length - 1 && (
                            <div className="col-start-1">
                              <AddButton
                                onClick={() =>
                                  handleAddField(sectionIdx, fieldIdx)
                                }
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
