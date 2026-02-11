import React, { useState, useRef, useEffect } from "react";

const TimePicker = ({
  time,
  setTime,
}: {
  time: {
    hour: string;
    minute: string;
  };
  setTime: React.Dispatch<
    React.SetStateAction<{
      hour: string;
      minute: string;
    }>
  >;
}) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  // 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(e.target as Node)
      ) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // 입력값 검증 및 업데이트
  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    type: "hour" | "minute",
  ) => {
    let val = e.target.value.replace(/[^0-9]/g, "");
    if (type === "hour" && Number(val) > 23) val = "00";
    if (type === "minute" && Number(val) > 59) val = "00";
    setTime({ ...time, [type]: val.slice(0, 2) });
  };

  return (
    <div className="relative w-full max-w-50" ref={wrapperRef}>
      {/* 입력창 영역: 클릭하면 열리고, 직접 타이핑도 가능 */}
      <div
        onClick={() => setIsOpen(true)}
        className={`flex items-center justify-between w-full h-15 px-4 bg-white rounded-xl
          outline-gray-300 focus-within:outline-2 ${isOpen && "outline-2"}`}
      >
        <div className="flex items-center space-x-1 body-l-medium  text-gray-700">
          <input
            type="text"
            value={time.hour}
            onChange={(e) => handleInputChange(e, "hour")}
            className="w-7 bg-transparent outline-none text-center"
            placeholder="09"
          />
          <span>:</span>
          <input
            type="text"
            value={time.minute}
            onChange={(e) => handleInputChange(e, "minute")}
            className="w-7 bg-transparent outline-none text-center"
            placeholder="00"
          />
        </div>
        <svg
          className="w-5 h-5 text-gray-400 cursor-pointer"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      </div>

      {/* 둥근 드롭다운 선택창 */}
      {isOpen && (
        <div className="absolute z-10 w-full mt-2 bg-white border border-gray-100 shadow-2xl rounded-2xl p-4 animate-in fade-in zoom-in duration-150">
          <div className="flex space-x-2 justify-center h-44">
            {/* 시(Hour) 선택 - 스크롤 스냅 적용 */}
            <div className="flex flex-col w-full overflow-y-auto snap-y snap-mandatory scrollbar-hide border-r border-gray-50">
              {Array.from({ length: 24 }).map((_, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() =>
                    setTime({ ...time, hour: i.toString().padStart(2, "0") })
                  }
                  className={`snap-center py-2 shrink-0 rounded-lg transition-colors ${
                    Number(time.hour) === i
                      ? "bg-blue-600 text-white font-bold"
                      : "hover:bg-blue-50 text-gray-500"
                  }`}
                >
                  {i.toString().padStart(2, "0")}
                </button>
              ))}
            </div>

            {/* 분(Minute) 선택 */}
            <div className="flex flex-col w-full overflow-y-auto snap-y snap-mandatory scrollbar-hide">
              {["00", "15", "30", "45"].map((m) => (
                <button
                  key={m}
                  type="button"
                  onClick={() => setTime({ ...time, minute: m })}
                  className={`snap-center py-2 shrink-0 rounded-lg transition-colors ${
                    time.minute === m
                      ? "bg-blue-600 text-white font-bold"
                      : "hover:bg-blue-50 text-gray-500"
                  }`}
                >
                  {m}
                </button>
              ))}
            </div>
          </div>

          <button
            onClick={() => setIsOpen(false)}
            type="button"
            className="cursor-pointer w-full mt-4 py-2.5 bg-gray-900 text-white text-sm font-semibold rounded-xl hover:bg-black active:scale-95 transition"
          >
            확인
          </button>
        </div>
      )}
    </div>
  );
};

export default TimePicker;
