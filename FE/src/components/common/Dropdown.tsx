import Button from "./Button";
import Tag from "./Tag";

const DropDown = ({
  type = "left",
  cols = 1,
  category = [{ name: "", items: [] }],
  dropdownOptions = [""],
  onInitClicked = () => {},
  onOptionClicked = (_a: number) => {},
  onApply = () => {},
}) => {
  const style = type === "left" ? "left-0" : type === "right" ? "right-0" : "";

  const gridStyle = cols === 5 ? `grid-cols-5` : `grid-cols-4`;

  return (
    <div
      className={`absolute top-20 rounded-[12px] flex flex-col w-max h-fit whitespace-nowrap
        bg-white z-10 drop-shadow-[0_0_7px_#6860A040] ${style}`}
    >
      <div className="p-9 border-b border-border-normal ">
        {category.length > 0 && (
          <div className="flex flex-row">
            {category.map((cate) => (
              <Tag>{cate.name}</Tag>
            ))}
          </div>
        )}
        <div className={`grid ${gridStyle} gap-3 `}>
          {dropdownOptions.map((opt, index) => (
            <Button onClick={() => onOptionClicked(index)} className="w-35">
              {opt}
            </Button>
          ))}
        </div>
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
