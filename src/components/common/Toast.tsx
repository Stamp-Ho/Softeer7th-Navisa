import { IcCheckContained } from "../../assets/icon/StratisUi";

type ToastProps = {
  message: string;
};

const Toast = ({ message }: ToastProps) => {
  return (
    <div
      className="fixed inset-0 z-50 flex justify-center top-[80%] pointer-events-none"
      role="status"
      aria-live="polite"
      aria-atomic="true"
    >
      <div className="flex justify-center items-center gap-3 min-w-87 h-14 rounded-[8px] bg-[rgba(47,52,61,0.8)] body-l-semibold text-gray-0">
        <IcCheckContained stroke="white" size={20} />
        {message}
      </div>
    </div>
  );
};

export default Toast;
