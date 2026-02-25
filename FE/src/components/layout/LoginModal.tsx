import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import NavisaLogo from "../../assets/NavisaLogo";
import Button from "../common/Button";
import Modal from "../common/Modal";
import { IcCheckBroken } from "../../assets/icon/StratisUi";
import TextInput from "../common/TextInput";
import { useLoginMutation } from "../../api/mutations/useLoginMutation";

const LoginModal = ({ onClose = () => {}, setAuthMode = (_a: number) => {} }) => {
  const { t } = useTranslation(["pages", "common"]);
  const loginMutation = useLoginMutation(onClose);
  const [stayLoggedIn, setStayLoggedIn] = useState<boolean>(true);
  const [email, setEmail] = useState<string>("");
  const [pw, setPw] = useState<string>("");
  const emailInputRef = useRef<HTMLInputElement>(null);
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => e.key === "Enter" && handleLogin(e as any);

    const modalElement = modalRef.current;
    modalElement?.addEventListener("keydown", handleKeyDown);

    return () => modalElement?.removeEventListener("keydown", handleKeyDown);
  }, [email, pw, loginMutation.isPending]);

  useEffect(() => {
    emailInputRef.current?.focus();
  }, []);

  const handleLogin = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    loginMutation.mutate({
      email: email,
      password: pw,
    });
  };
  return (
    <Modal ref={modalRef} className="flex flex-col items-center px-10 py-20" onClose={onClose}>
      <NavisaLogo height={10} />
      <TextInput
        className="mt-21 mb-3"
        ref={emailInputRef}
        placeholder={t("auth.login.email")}
        value={email}
        setValue={setEmail}
      />
      <TextInput className="mb-3" placeholder={t("auth.login.password")} type="password" value={pw} setValue={setPw} />
      <a
        onClick={() => setStayLoggedIn((prev) => !prev)}
        className={`flex flex-row gap-2 w-full items-center cursor-pointer mb-10 rounded-sm ${stayLoggedIn ? "text-primary" : `text-text-sub`}`}
        tabIndex={0}
      >
        <IcCheckBroken activated={stayLoggedIn} />
        {t("auth.login.rememberMe")}
      </a>
      <Button
        className="w-full mb-6"
        variant={"primary"}
        size="large"
        onClick={handleLogin}
        disabled={loginMutation.isPending}
        type="button"
      >
        {loginMutation.isPending ? t("auth.login.loggingIn") : t("auth.login.loginButton")}
      </Button>

      <div className="flex flex-row body-s-medium text-text-sub gap-3 pb-20">
        {/* <a className="cursor-pointer">{t("auth.login.forgotPassword")}</a>
        <div className="border-r border-gray-200 h-2 w-px mt-auto mb-auto"></div>
        <a className="cursor-pointer">{t("auth.login.forgotEmail")}</a>
        <div className="border-r border-gray-200 h-2 w-px mt-auto mb-auto"></div> */}
        <a className="cursor-pointer rounded-sm px-1" onClick={() => setAuthMode(2)} tabIndex={0}>
          {t("auth.signup.title")}
        </a>
      </div>
    </Modal>
  );
};

export default LoginModal;
