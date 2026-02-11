import type { FormSection } from "../../types/formType";
import FormField from "./FormField";
import { useState } from "react";
import ImageUploadInput from "./inputComponents/ImageUploadInput";

const NavisaForm = ({
  formData,
  addIndex = false,
  startsWithImage = false,
  imageFile,
  setImageFile,
}: {
  formData: FormSection[];
  addIndex?: boolean;
  startsWithImage?: boolean;
  isAgent?: boolean;
  imageFile?: File | undefined;
  setImageFile?: React.Dispatch<React.SetStateAction<File | undefined>>;
}) => {
  const [formStruct, setFormStruct] = useState(formData);
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
          {startsWithImage &&
            setImageFile !== undefined &&
            sectionIdx === 0 && (
              <ImageUploadInput
                placeholder={
                  formStruct[0].fields[0].inputLines[0].inputs[0].placeholder ??
                  ""
                }
                imageFile={imageFile}
                setImageFile={setImageFile}
              />
            )}
          {section.fields
            .slice(startsWithImage && sectionIdx === 0 ? 1 : 0)
            .map((inputField, fieldIdx) => (
              <FormField
                key={`field_${sectionIdx}_${fieldIdx + (startsWithImage && sectionIdx === 0 ? 1 : 0)}`}
                inputField={inputField}
                sectionIdx={sectionIdx}
                fieldIdx={
                  fieldIdx + (startsWithImage && sectionIdx === 0 ? 1 : 0)
                }
                addIndex={addIndex}
                startsWithImage={startsWithImage}
                setFormStruct={setFormStruct}
              />
            ))}
        </div>
      ))}
    </div>
  );
};

export default NavisaForm;
