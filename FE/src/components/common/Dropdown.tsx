import { useEffect, useRef, useState } from "react";
import Button from "./Button";
import type { DropDownProps } from "../../types/dropdownProps";

const DropDown = ({
  type = "left",
  cols = 1,
  category,
  dropdownOptions = [""],
  onInitClicked = () => {},
  onOptionClicked = (a: number) => {
    alert(a);
  },
  onApply = () => {},
}: DropDownProps) => {
  const [selectedCategoryIdx, setSelectedCategoryIdx] = useState(0);

  const style = type === "left" ? "left-0" : type === "right" ? "right-0" : "";

  const gridStyle = cols === 5 ? `grid-cols-5` : `grid-cols-4`;

  const scrollRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const scrollContainer = scrollRef.current;
    if (scrollContainer) {
      const onWheel = (event: WheelEvent) => {
        event.preventDefault();
        if (Math.abs(event.deltaX) > Math.abs(event.deltaY))
          scrollContainer.scrollLeft += event.deltaX;
        else scrollContainer.scrollLeft += event.deltaY;
      };

      scrollContainer.addEventListener("wheel", onWheel);
      return () => {
        scrollContainer.removeEventListener("wheel", onWheel);
      };
    }
  }, []);

  return (
    <div
      className={`absolute top-20 rounded-xl flex flex-col w-max h-fit whitespace-nowrap
        bg-white z-10 shadow ${style}`}
    >
      <div className="p-9 border-b border-border-normal ">
        {category && (
          <div
            ref={scrollRef}
            className="mb-5 flex max-w-147 flex-row items-center gap-5 overflow-x-auto scrollbar-hide"
          >
            {category.map((cate, idx) => {
              const isSelected = selectedCategoryIdx === idx;
              return (
                <button
                  key={cate.name}
                  onClick={() => setSelectedCategoryIdx(idx)}
                  className={`
                    flex h-11 shrink-0 cursor-pointer items-center rounded-full px-4 body-l-semibold transition-colors
                    ${isSelected ? "bg-violet-50-transpar text-violet-500" : "text-text-sub"}
                    `}
                >
                  {cate.name}
                </button>
              );
            })}
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
