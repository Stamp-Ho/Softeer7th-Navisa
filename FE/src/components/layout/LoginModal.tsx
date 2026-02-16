import { useState } from "react";
import NavisaLogo from "../../assets/NavisaLogo";
import Button from "../common/Button";
import Modal from "../common/Modal";
import { IcCheckBroken } from "../../assets/icon/StratisUi";
import TextInput from "../common/TextInput";
import { useLoginMutation } from "../../api/mutations/useLoginMutation";

const LoginModal = ({
  onClose = () => {},
  setAuthMode = (_a: number) => {},
}) => {
  const loginMutation = useLoginMutation(onClose);
  const [stayLoggedIn, setStayLoggedIn] = useState<boolean>(false);
  const [email, setEmail] = useState<string>("feTest0001@example.com");
  const [pw, setPw] = useState<string>("test1234");

  const handleLogin = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    loginMutation.mutate({
      email: email,
      password: pw,
    });
  };
  return (
    <Modal className="flex flex-col items-center px-10 py-20" onClose={onClose}>
      <NavisaLogo height={10} />
      <TextInput
        className="mt-21 mb-3"
        placeholder="아이디 (이메일)"
        value={email}
        setValue={setEmail}
      />
      <TextInput
        className="mb-3"
        placeholder="비밀번호"
        type="password"
        value={pw}
        setValue={setPw}
      />
      <a
        onClick={() => setStayLoggedIn((prev) => !prev)}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-10 ${stayLoggedIn ? "text-primary" : `text-text-sub`}`}
      >
        <IcCheckBroken activated={stayLoggedIn} />
        로그인 상태 유지
      </a>
      <Button
        className="w-full mb-6"
        type={"primary"}
        onClick={handleLogin}
        disabled={loginMutation.isPending}
      >
        {loginMutation.isPending ? "로그인 중..." : "로그인"}
      </Button>
      <div className="flex flex-row body-s-medium text-text-sub gap-3 pb-20">
        <a className="cursor-pointer">비밀번호 찾기</a>
        <div className="border-r border-gray-200 h-2 w-px mt-auto mb-auto"></div>
        <a className="cursor-pointer">아이디 찾기</a>
        <div className="border-r border-gray-200 h-2 w-px mt-auto mb-auto"></div>
        <a className="cursor-pointer" onClick={() => setAuthMode(2)}>
          회원가입
        </a>
      </div>
    </Modal>
  );
};

export default LoginModal;
