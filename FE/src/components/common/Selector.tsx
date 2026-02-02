import { useState } from "react";
import { IcArrows } from "../../assets/icon/StratisUi";

const Selector = ({
  value = "",
  placeholder = "",
  options = [""],
  className = "",
}) => {
  const [isOpen, setIsOpen] = useState(false);

  const zOfSelector = isOpen ? "z-21" : "";
  const zOfDropdown = isOpen ? "z-20" : "";

  return (
    <div className="relative h-15">
      <div
        className={`absolute left-0 top-0 px-4 py-5 rounded-lg
        flex items-center w-full h-15
        body-l-medium
        ${isOpen && " border border-border-normal"}
        ${zOfSelector}
        ${value ? "bg-violet-25 text-primary border-primary" : "bg-background-default text-text-base"}
        ${className}`}
      >
        {value === "" ? (
          <a className="text-text-sub">{placeholder}</a>
        ) : (
          <a className="text-text-base">{value}</a>
        )}
        <div className="ml-auto cursor-pointer">
          <div
            onClick={() => setIsOpen((prev) => !prev)}
            className={`transition-transform ${isOpen ? "rotate-180" : ""}`}
          >
            <IcArrows stroke={value ? "var(--primary)" : "#b1b5bc"} />
          </div>
        </div>
      </div>

      {/* dropdown */}
      {isOpen && (
        <div
          className={`
            absolute left-0 top-full -mt-2.5 pt-4 pb-1 pr-1
            overflow-y-hidden
            h-75 w-full
            bg-white rounded-lg
            shadow
            rounded-t-none
            ${zOfDropdown}
          `}
        >
          <div className="flex flex-col gap-0.5 overflow-y-auto scrolltrack-hide h-full">
            {options.map((opt, index) => (
              <div
                className="py-1.75 flex flex-row items-center pl-5 cursor-pointer hover:bg-gray-50"
                onClick={() => {
                  alert(index);
                }}
              >
                {opt}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Selector;
