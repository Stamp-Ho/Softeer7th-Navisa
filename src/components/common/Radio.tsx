import { useState } from "react";

const Radio = ({ className = "", options = [""] }) => {
  const [value, setValue] = useState(-1);
  return (
    <div className={`h-14 flex flex-row ${className}`}>
      {options.map((opt, index) => (
        <div
          key={`radio_${index}`}
          className={`${index === 0 ? "rounded-l-[10px] " : index === options.length - 1 ? "rounded-r-[10px] -ml-px" : "-ml-px"}
            body-l-medium border cursor-pointer
            w-full flex items-center justify-center
            ${index === value ? "border-violet-200 bg-violet-50 text-primary z-0" : "text-text-sub  bg-white border-border-normal"}
          `}
          onClick={() => setValue(index)}
        >
          {opt}
        </div>
      ))}
    </div>
  );
};
export default Radio;
