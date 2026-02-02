import type React from "react";
import { IcArrows } from "../../assets/icon/StratisUi";
import { useState } from "react";
import DropDown from "./Dropdown";

const FilterWithDropdown = ({
  isActive = false,
  className,
  children,
  category,
  cols,
  dropdownOptions,
  dropdownAlign = "left",
  onOptionClicked,
}: {
  isActive?: boolean;
  className?: string;
  children?: React.ReactNode;
  category?: { name: string; items: string[] }[];
  cols?: number;
  dropdownOptions?: string[];
  dropdownAlign?: string;
  onOptionClicked?: (a: number) => void;
}) => {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <div
      className={`px-7 py-5 border border-border-normal bg-background-default rounded-lg
         body-l-semibold flex flex-row items-center relative
        ${isActive ? "bg-violet-25 text-primary border-primary" : "text-text-base"} ${className}`}
    >
      {children}
      <div className="ml-auto cursor-pointer">
        <div
          onClick={() => setIsOpen((prev) => !prev)}
          className={`transition-transform ${isOpen ? "rotate-180" : ""}`}
        >
          <IcArrows stroke={isActive ? "var(--primary)" : "#b1b5bc"} />
        </div>
      </div>
      {isOpen && (
        <DropDown
          cols={cols}
          dropdownOptions={dropdownOptions}
          category={category}
          onOptionClicked={onOptionClicked}
          type={dropdownAlign}
          onApply={() => {
            setIsOpen(false);
          }}
        />
      )}
    </div>
  );
};

export default FilterWithDropdown;
