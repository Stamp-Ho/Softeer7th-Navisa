import { useState } from "react";
import { AgentButton, ForeignerButton } from "../../assets/RegistrationButtons";
import Modal from "../common/Modal";
import Button from "../common/Button";
import SignUpForm from "./SignUpForm";
const SignUpModal = ({ onClose = () => {} }) => {
  const [userType, setUserType] = useState<number>(0); //0: none, 1: foreigner, 2:agent
  const [signUpStep, setSignUpStep] = useState<number>(1);
  return (
    <Modal
      className="flex flex-col items-center px-10 py-20 text-text-base"
      onClose={onClose}
    >
      {signUpStep === 1 ? (
        <>
          <a className="title-l-semibold mb-19 mt-4">
            어떤 유형의 회원이신가요?
          </a>
          <div className="flex flex-row gap-10">
            <div className="flex flex-col items-center gap-2">
              <ForeignerButton
                onClick={() => setUserType(1)}
                isActive={userType === 1}
              />
              <a className="body-s-medium mt-4">비자를 발급받고 싶은</a>
              <a className="title-s-semibold">외국인 회원</a>
            </div>
            <div className="flex flex-col items-center gap-2">
              <AgentButton
                onClick={() => setUserType(2)}
                isActive={userType === 2}
              />
              <a className="body-s-medium mt-4">비자 발급을 도와주는</a>
              <a className="title-s-semibold">행정사 회원</a>
            </div>
          </div>
          <Button
            className="w-full mt-25 -mb-5"
            onClick={() => {
              // 구글 인증에 성공하여, token이 넘어온 경우, 바로 완료
              // 그게 아니면 다음 단계로 이동
              setSignUpStep((prev) => prev + 1);
            }}
            disabled={userType === 0}
          >
            다음
          </Button>
        </>
      ) : (
        <SignUpForm
          onSubmit={() => {
            onClose();
          }}
        />
      )}
    </Modal>
  );
};

export default SignUpModal;
