import { forwardRef } from "react";
import { useTranslation } from "react-i18next";
import type { InputHTMLAttributes } from "react";

interface TextInputProps extends InputHTMLAttributes<HTMLInputElement> {
  className?: string;
  isInvalid?: boolean;
  invalidMsg?: string;
  // setValue는 hook form 사용 시 선택 사항이 되므로 옵셔널로 변경하거나 제외 가능
  setValue?: (val: string) => void;
  placeholderKey?: string;
}

const TextInput = forwardRef<HTMLInputElement, TextInputProps>(
  (
    {
      className = "",
      placeholder = "",
      type = "text",
      isInvalid = false,
      invalidMsg = "",
      value,
      setValue,
      onChange,
      placeholderKey = "input.placeholder",
      ...props // 나머지 속성(name, onBlur 등)을 input에 전달
    },
    ref,
  ) => {
    const { t } = useTranslation(["common"]);
    const displayPlaceholder = placeholder || t(placeholderKey);
    return (
      <div className="w-full flex flex-col relative">
        {isInvalid && <span className="absolute text-red-500 text-xs bottom-px left-5">{invalidMsg}</span>}
        <input
          ref={ref} // forwardRef로 받은 ref를 여기에 연결
          className={`w-full px-5 bg-gray-50 rounded-xl
          text-[16px] font-medium focus:outline-gray-300 focus:outline-2 h-14
          placeholder:text-gray-400 ${className}
          ${isInvalid ? "outline-2 outline-red-400 focus:outline-red-400" : ""}`}
          placeholder={displayPlaceholder}
          type={type}
          value={value}
          onChange={onChange ?? ((e) => setValue?.(e.target.value))}
          {...props}
        />
      </div>
    );
  },
);

TextInput.displayName = "TextInput";

export default TextInput;
