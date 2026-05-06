import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { AgentButton, ForeignerButton } from "../../assets/RegistrationButtons";
import Modal from "../common/Modal";
import Button from "../common/Button";
import SignUpForm from "../form/SignUpForm";
const SignUpModal = ({ onClose = () => {} }) => {
  const { t } = useTranslation(["pages"]);
  const [userType, setUserType] = useState<number>(0); //0: none, 1: foreigner, 2:agent
  const [signUpStep, setSignUpStep] = useState<number>(1);

  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const modal = modalRef.current;
    if (!modal) return;

    // 모달이 마운트될 때 첫 번째 포커스 가능한 요소로 포커스 이동
    const focusableElements = modal.querySelectorAll(
      'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])',
    );
    const firstElement = focusableElements[signUpStep - 1] as HTMLElement;
    if (firstElement) {
      firstElement.focus();
    }
  }, [signUpStep]);
  return (
    <Modal ref={modalRef} className="flex flex-col items-center px-10 py-20 text-text-base" onClose={onClose}>
      {signUpStep === 1 ? (
        <>
          <a className="title-l-semibold mb-19 mt-4">{t("auth.userType.question")}</a>
          <div className="flex flex-row gap-10">
            <div className="flex flex-col items-center gap-2">
              <ForeignerButton onClick={() => setUserType(1)} isActive={userType === 1} />
              <a className="body-s-medium mt-4">{t("auth.userType.foreignerDescription")}</a>
              <a className="title-s-semibold">{t("auth.userType.foreigner")}</a>
            </div>
            <div className="flex flex-col items-center gap-2">
              <AgentButton onClick={() => setUserType(2)} isActive={userType === 2} />
              <a className="body-s-medium mt-4">{t("auth.userType.agentDescription")}</a>
              <a className="title-s-semibold">{t("auth.userType.agent")}</a>
            </div>
          </div>
          <Button
            className="w-full mt-25 -mb-5"
            variant="primary"
            onClick={() => {
              setSignUpStep((prev) => prev + 1);
            }}
            disabled={userType === 0}
            tabIndex={0}
          >
            {t("auth.userType.next")}
          </Button>
        </>
      ) : (
        <SignUpForm
          isAgent={userType === 2}
          onSubmit={() => {
            onClose();
          }}
        />
      )}
    </Modal>
  );
};

export default SignUpModal;
