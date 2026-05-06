import { forwardRef, useEffect } from "react";
import { IcX } from "../../assets/icon/StratisUi";
import type { ModalProps } from "../../types/modalProps";
import { useTranslation } from "react-i18next";

const Modal = forwardRef<HTMLDivElement, Omit<ModalProps, "ref">>(({ children, className, onClose }, ref) => {
  const location = window.location.pathname;
  const scrollPadding =
    location.startsWith("/search") || location.startsWith("/chat") || location.startsWith("/document");
  useEffect(() => {
    const handleTabKey = (e: KeyboardEvent) => {
      if (e.key !== "Tab") return;

      const focusableSelector = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';
      const allElements = Array.from((ref as any)?.current?.querySelectorAll(focusableSelector) || []) as HTMLElement[];

      // disabled와 hidden 체크
      const focusableElements = allElements.filter((el) => {
        if ((el as HTMLButtonElement | HTMLInputElement).disabled) return false; // disabled 제외
        if (el.offsetParent === null) return false; // 숨겨진 요소 제외
        return true;
      });
      if (focusableElements.length === 0) return;

      const firstElement = focusableElements[0] as HTMLElement;
      const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;
      const activeElement = document.activeElement;

      if (e.shiftKey) {
        if (activeElement === firstElement) {
          e.preventDefault();
          lastElement.focus();
        }
      } else {
        if (activeElement === lastElement) {
          e.preventDefault();
          firstElement.focus();
        }
      }
    };
    // 모달이 열릴 때 스크롤 방지
    document.body.style.overflow = "hidden";
    scrollPadding || (document.body.style.paddingRight = "8px"); // 스크롤바 너비만큼 패딩 추가 (선택 사항)

    const modalElement = (ref as any)?.current;
    modalElement?.addEventListener("keydown", handleTabKey);

    return () => {
      modalElement?.removeEventListener("keydown", handleTabKey);
      document.body.style.overflow = "auto"; // 복원
      scrollPadding || (document.body.style.paddingRight = "0"); // 패딩 제거
    };
  }, []);

  const { t } = useTranslation(["common"]);
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30" ref={ref}>
      <div
        className={`bg-white p-5 rounded-[20px] flex flex-col items-center w-124
          shadow`}
      >
        {onClose && (
          <button
            className="ml-auto flex justify-end cursor-pointer"
            onClick={onClose}
            title={t("button.close")}
            tabIndex={1}
            type="button"
          >
            <IcX />
          </button>
        )}

        <div className={`w-full ${className}`}>{children}</div>
      </div>
    </div>
  );
});

Modal.displayName = "Modal";

export default Modal;
