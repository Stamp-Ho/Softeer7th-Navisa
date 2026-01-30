import { IcDash, IcDot, IcPlus } from "../../assets/icon/StratisUi";
import type { input } from "../../types/formType";
import Radio from "../common/Radio";
import Selector from "../common/Selector";
import TextArea from "../common/TextArea";
import TextInput from "../common/TextInput";
import TimePicker from "../common/TimePicker";

const InputRenderer = ({
  input,
  className,
}: {
  input: input;
  className?: string;
}) => {
  //const { register } = useFormContext(); // react-hook-form 연결

  switch (input.inputType) {
    case "text":
      return (
        <TextInput
          type="text"
          placeholder={input.placeholder}
          className={`bg-white ${className}`}
        />
      ); // {...register(input.fieldName)}/>;
    case "longText":
      return (
        <>
          <TextInput
            type="text"
            placeholder={input.placeholder}
            className={`bg-white ${className}`}
          />
          <a className="ml-auto body-m-regular text-gray-400 mt-1 -mb-6">
            {0}/{50}
          </a>
        </>
      );
    case "selector":
      return (
        <Selector
          options={input.options}
          placeholder={input.placeholder}
          className={className}
        /> // name={input.fieldName}/>
      );
    case "radio":
      return <Radio options={input.options} />;
    case "date":
      return (
        <div className="grid grid-cols-3 gap-3">
          <Selector placeholder="YYYY" /> <Selector placeholder="MM" />{" "}
          <Selector placeholder="DD" />
        </div>
      );
    case "textArea":
      return <TextArea placeholder={input.placeholder} />;
    case "timeRange":
      return (
        <div className="flex flex-row items-center gap-3">
          <TimePicker isStart={true} />
          <IcDash size={36} />
          <TimePicker />
        </div>
      );
    case "image":
      return (
        <div className="grid-cols-3 flex flex-row gap-5">
          <div className="rounded-xl flex items-center justify-center bg-white w-52.5 h-67.5">
            {false ? (
              <image />
            ) : (
              <div>
                <IcPlus />
              </div>
            )}
          </div>
          <div className="flex flex-col justify-end h-67.5 title-s-medium text-gray-400">
            <h4 className="title-l-semibold text-text-base mb-2">
              사진 업로드하기
            </h4>
            <h5 className="flex flex-row items-center gap-0.5">
              <IcDot size={24} />
              {input.placeholder}
            </h5>
            <h5 className="flex flex-row items-center gap-0.5">
              <IcDot size={24} />
              규격 안내
            </h5>
            <h5 className="flex flex-row items-center gap-0.5">
              <IcDot size={24} />
              파일 크기 안내
            </h5>
          </div>
        </div>
      );
    // ... 나머지 케이스
    default:
      return null;
  }
};
export default InputRenderer;
