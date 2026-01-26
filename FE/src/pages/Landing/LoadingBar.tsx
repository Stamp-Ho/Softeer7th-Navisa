import { useState, useEffect } from "react";

const messages = [
  "상세 요건 분석 중",
  "행정사 목록 불러오는 중",
  "당신에게 꼭 맞는 행정사 탐색 중",
  "조금만 더 기다려주세요",
  "거의 다 되었습니다!",
];
const LoadingBar = ({ isCompleted = false }) => {
  const [step, setStep] = useState<number>(0);
  const [rolling, setRolling] = useState<boolean>(false);
  const [messageIdx, setMessageIdx] = useState<number>(0);
  const totalStep = 4;

  useEffect(() => {
    if (step >= totalStep) return;

    const timer = setTimeout(() => {
      setRolling(true);
      setStep((prev) => prev + 1);
      setTimeout(() => {
        setRolling(false);
        setMessageIdx((prev) => prev + 1);
      }, 750);
    }, 1000);

    return () => clearTimeout(timer);
  }, [step]);

  const [rendered, setRendered] = useState<boolean>(false);
  useEffect(() => {
    setRendered(true);
  }, []);

  const rollAnimationStyle = rolling
    ? "transition-all -translate-y-6 duration-750 ease-in-out"
    : "transition-none translate-y-0";
  return (
    <div
      className={`flex flex-col mx-138 items-center gap-4 transition-all duration-750 ease-in-out ${
        isCompleted
          ? "opacity-0 translate-y-2 pointer-events-none"
          : "opacity-100"
      }`}
    >
      <div className="w-100 h-4 rounded-lg overflow-hidden relative bg-linear-to-r from-green-100 to-violet-100">
        <div
          style={{ width: rendered ? 0 : "calc(100%)" }}
          className={`absolute right-0 h-4 bg-gray-150 transition-all duration-5000`}
        />
      </div>
      <div className="overflow-hidden h-6">
        <ul
          className={`${rollAnimationStyle} min-w-0 flex flex-col items-center`}
        >
          <li className="body-m-medium text-gray-600 h-6">
            {messages[messageIdx] || messages[messages.length - 1]}
          </li>
          <li className="body-m-medium text-gray-600 h-6">
            {messages[messageIdx + 1] || messages[messages.length - 1]}
          </li>
        </ul>
      </div>
    </div>
  );
};

export default LoadingBar;
