import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";

const LoadingBar = ({ isCompleted = false }) => {
  const { t } = useTranslation(["pages"]);
  const messages = [
    t("landing.loadingStep1"),
    t("landing.loadingStep2"),
    t("landing.loadingStep3"),
    t("landing.loadingStep5"),
  ];
  const [step, setStep] = useState<number>(0);
  const [rolling, setRolling] = useState<boolean>(false);
  const [messageIdx, setMessageIdx] = useState<number>(0);
  const totalStep = 3;

  useEffect(() => {
    if (step >= totalStep) return;

    const timer = setTimeout(() => {
      setRolling(true);
      setStep((prev) => prev + 1);
      setTimeout(() => {
        setRolling(false);
        setMessageIdx((prev) => prev + 1);
      }, 300);
    }, 600);

    return () => clearTimeout(timer);
  }, [step]);

  const [rendered, setRendered] = useState<boolean>(false);
  useEffect(() => {
    setRendered(true);
  }, []);

  const rollAnimationStyle = rolling
    ? "transition-all -translate-y-6 duration-300 ease-in-out"
    : "transition-none translate-y-0";
  return (
    <div
      className={`flex flex-col mx-138 items-center gap-4 transition-all duration-300 ease-in-out ${isCompleted ? "opacity-0 translate-y-2 pointer-events-none" : "opacity-100"}`}
    >
      <div className="w-100 h-4 rounded-lg overflow-hidden relative bg-linear-to-r from-green-100 to-violet-100">
        <div
          style={{ width: rendered ? 0 : "calc(100%)" }}
          className={`absolute right-0 h-4 bg-gray-150 transition-all duration-2000`}
        />
      </div>
      <div className="overflow-hidden h-6">
        <ul className={`${rollAnimationStyle} min-w-0 flex flex-col items-center`}>
          <li className="body-m-medium text-gray-600 h-6">{messages[messageIdx] || messages[messages.length - 1]}</li>
          <li className="body-m-medium text-gray-600 h-6">
            {messages[messageIdx + 1] || messages[messages.length - 1]}
          </li>
        </ul>
      </div>
    </div>
  );
};

export default LoadingBar;
