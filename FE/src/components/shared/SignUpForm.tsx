import { useState } from "react";
import TextInput from "../common/TextInput";
import { IcCheck, IcCheckBroken } from "../../assets/icon/StratisUi";
import Button from "../common/Button";

const VALIDATOR = {
  email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  password: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
};
const SignUpForm = ({ onSubmit = () => {} }: { onSubmit: () => void }) => {
  const [email, setEmail] = useState<string>("");
  const [pw, setPw] = useState<string>("");
  const [pwCheck, setPwCheck] = useState<string>("");

  const [option1, setOption1] = useState<boolean>(false);
  const [option2, setOption2] = useState<boolean>(false);
  const [option3, setOption3] = useState<boolean>(false);

  const onTotalClick = () => {
    const targetValue = !(option1 && option2 && option3);
    setOption1(targetValue);
    setOption2(targetValue);
    setOption3(targetValue);
  };

  const warnWhenWrong = "outline-2 outline-red-400 focus:outline-red-400 ";

  const validEmail = VALIDATOR.email.test(email) || email === "";
  const validPw = VALIDATOR.password.test(pw) || pw === "";
  const validPwCheck =
    (VALIDATOR.password.test(pw) && pw === pwCheck) || pwCheck === "";

  return (
    <>
      <h2 className="title-l-semibold text-text-base mb-8 -mt-12">회원가입</h2>
      <label className="mr-auto mb-3">이메일</label>
      <TextInput
        value={email}
        setValue={setEmail}
        placeholder="navisa@gmail.com"
        className={validEmail ? "" : warnWhenWrong}
      />
      {validEmail || (
        <a className="text-red-500 -mt-4 text-xs">
          올바른 이메일 형식이 아닙니다.
        </a>
      )}
      <label className="mr-auto mb-3 mt-6">비밀번호</label>
      <TextInput
        value={pw}
        setValue={setPw}
        placeholder="영문, 숫자를 모두 포함해서 8자 이상"
        type="password"
        className={`mb-3 ${validPw ? "" : warnWhenWrong}`}
      />
      {validPw || (
        <a className="text-red-500 -mt-7 mb-3 text-xs">
          영문, 숫자를 모두 포함해서 8자 이상으로 설정해주세요.
        </a>
      )}
      <TextInput
        value={pwCheck}
        setValue={setPwCheck}
        placeholder="비밀번호 재확인"
        type="password"
        className={`mb-8 ${validPwCheck ? "" : warnWhenWrong}`}
      />
      {validPwCheck || (
        <a className="text-red-500 -mt-12 mb-8 text-xs">
          비밀번호가 일치하지 않거나 올바르지 않습니다.
        </a>
      )}
      <a
        onClick={onTotalClick}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-3 ${option1 && option2 && option3 ? "text-primary" : `text-text-sub`}`}
      >
        <IcCheckBroken activated={option1 && option2 && option3} />
        모두 동의합니다
      </a>
      <a
        onClick={() => setOption1((prev) => !prev)}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-1.5 ${option1 ? "text-primary" : `text-text-sub`}`}
      >
        <IcCheck activated={option1} />
        (필수) 어쩌구 약관에 동의합니다
      </a>
      <a
        onClick={() => setOption2((prev) => !prev)}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-1.5 ${option2 ? "text-primary" : `text-text-sub`}`}
      >
        <IcCheck activated={option2} />
        (필수) 저쩌구 약관에 동의합니다
      </a>
      <a
        onClick={() => setOption3((prev) => !prev)}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-7.25 ${option3 ? "text-primary" : `text-text-sub`}`}
      >
        <IcCheck activated={option3} />
        (선택) 궁시렁 약관에 동의합니다
      </a>
      <Button
        className="w-full -mb-8"
        onClick={() => {
          onSubmit();
        }}
        disabled={
          !(
            option1 &&
            option2 &&
            VALIDATOR.email.test(email) &&
            VALIDATOR.password.test(pw) &&
            pw === pwCheck
          )
        }
      >
        가입 완료
      </Button>
    </>
  );
};
export default SignUpForm;
