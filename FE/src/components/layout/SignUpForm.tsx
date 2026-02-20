import TextInput from "../common/TextInput";
import Button from "../common/Button";
import { useTranslation } from "react-i18next";
import { useSignUpMutation } from "../../api/mutations/useSignUpMutation";
import CheckBox from "../common/CheckBox";
import CheckLine from "../common/CheckLine";
import { useForm } from "react-hook-form";

const VALIDATOR = {
  email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  password: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
};
type SignUpFormData = {
  email: string;
  pw: string;
  pwCheck: string;
  option1: boolean;
  option2: boolean;
  option3: boolean;
};

const SignUpForm = ({ isAgent = false, onSubmit = () => {} }) => {
  const { t } = useTranslation(["pages", "common"]);
  const signupMutation = useSignUpMutation(() => {
    onSubmit();
  });
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isValid },
  } = useForm<SignUpFormData>({
    mode: "onChange", // 실시간 유효성 검사를 위해 설정
    defaultValues: { option1: false, option2: false, option3: false },
  });
  const [pw, opt1, opt2, opt3] = watch(["pw", "option1", "option2", "option3"]);

  const onTotalClick = () => {
    const isAllChecked = opt1 && opt2 && opt3;
    setValue("option1", !isAllChecked);
    setValue("option2", !isAllChecked);
    setValue("option3", !isAllChecked);
  };

  const onInternalSubmit = (data: SignUpFormData) => {
    signupMutation.mutate({
      email: data.email,
      password: data.pw,
      userType: isAgent ? "INVALID_AGENT" : "UNFILLED_FOREIGNER",
    });
  };

  return (
    <form onSubmit={handleSubmit(onInternalSubmit)} className="flex flex-col w-full gap-3">
      <h2 className="title-l-semibold text-text-base mb-5 -mt-12">{t("auth.signup.title")}</h2>

      <label className="mr-auto">{t("auth.signup.email")}</label>
      <TextInput
        {...register("email", {
          required: true,
          pattern: {
            value: VALIDATOR.email,
            message: t("auth.validation.invalidEmail"),
          },
        })}
        placeholder="navisa@gmail.com"
        isInvalid={!!errors.email}
        invalidMsg={errors.email?.message}
      />
      <label className="mr-auto mt-2">{t("auth.signup.password")}</label>
      <TextInput
        {...register("pw", {
          required: true,
          pattern: {
            value: VALIDATOR.password,
            message: t("auth.validation.passwordRequirement"),
          },
        })}
        type="password"
        placeholder={t("auth.signup.password")}
        isInvalid={!!errors.pw}
        invalidMsg={errors.pw?.message}
      />
      <TextInput
        {...register("pwCheck", {
          required: true,
          validate: (value) => value === pw || t("auth.validation.passwordMismatch"),
        })}
        type="password"
        placeholder={t("auth.signup.confirmPassword")}
        isInvalid={!!errors.pwCheck}
        invalidMsg={errors.pwCheck?.message}
      />
      <div className="mt-8 flex flex-col">
        <CheckBox label={t("auth.signup.agreeAll")} value={opt1 && opt2 && opt3} setValue={onTotalClick} />
        <CheckLine label={t("auth.signup.termsAgreement")} value={opt1} setValue={() => setValue("option1", !opt1)} />
        <CheckLine label={t("auth.signup.privacyAgreement")} value={opt2} setValue={() => setValue("option2", !opt2)} />
        <CheckLine
          label={t("auth.signup.marketingAgreement")}
          value={opt3}
          setValue={() => setValue("option3", !opt3)}
          className="mb-4"
        />
      </div>

      <Button
        className="w-full -mb-8"
        variant="primary"
        disabled={!isValid || !(opt1 && opt2)} // 필수 동의 체크 여부 포함
        type="submit"
      >
        {t("auth.signup.complete")}
      </Button>
    </form>
  );
};
export default SignUpForm;
