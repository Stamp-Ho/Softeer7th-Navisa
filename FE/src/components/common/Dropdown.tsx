import { useState } from "react";
import Button from "./Button";

type DropDownParams = {
  type: string;
  cols?: number;
  category?: { name: string; items: string[] }[];
  dropdownOptions?: string[];
  onInitClicked?: () => void;
  onOptionClicked?: (arg: number) => void;
  onApply: () => void;
};

const DropDown = ({
  type = "left",
  cols = 1,
  category,
  dropdownOptions = [""],
  onInitClicked = () => {},
  onOptionClicked = (_a: number) => {},
  onApply = () => {},
}: DropDownParams) => {
  const [selectedCategoryIdx, setSelectedCategoryIdx] = useState(0);

  const style = type === "left" ? "left-0" : type === "right" ? "right-0" : "";

  const gridStyle = cols === 5 ? `grid-cols-5` : `grid-cols-4`;

  return (
    <div
      className={`absolute top-20 rounded-[12px] flex flex-col w-max h-fit whitespace-nowrap
        bg-white z-10 drop-shadow-[0_0_7px_#6860A040] ${style}`}
    >
      <div className="p-9 border-b border-border-normal ">
        {category && (
          <div className="mb-5 max-w-147 flex-wrap overflow-x-auto scrollbar-hide">
            <div className="flex flex-row gap-5 items-center">
              {category.map((cate, idx) => (
                <button
                  key={cate.name}
                  onClick={() => setSelectedCategoryIdx(idx)}
                >
                  <div
                    className={`${selectedCategoryIdx === idx ? "text-violet-500 bg-violet-50-transpar" : "text-text-sub"} 
                  flex items-center px-4 h-11 rounded-full cursor-pointer body-l-semibold`}
                  >
                    {cate.name}
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}
        {category ? (
          <div className={`grid ${gridStyle} gap-3`}>
            {category[selectedCategoryIdx]?.items.map((opt, index) => (
              <Button onClick={() => onOptionClicked(index)} className="w-35">
                {opt}
              </Button>
            ))}
          </div>
        ) : (
          <div className={`grid ${gridStyle} gap-3 `}>
            {dropdownOptions.map((opt, index) => (
              <Button onClick={() => onOptionClicked(index)} className="w-35">
                {opt}
              </Button>
            ))}
          </div>
        )}
      </div>
      <div className="flex flex-row ml-auto gap-3 px-9 py-5">
        <Button type="grayLine" className="w-30" onClick={onInitClicked}>
          초기화
        </Button>
        <Button type="primary" className="w-30" onClick={onApply}>
          적용하기
        </Button>
      </div>
    </div>
  );
};
export default DropDown;
