import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { RPDocument, RPMessage, RPPeople } from "../../../assets/icon/RecommendPanelIcon";
import { IcPencilLine } from "../../../assets/icon/StratisUi";
import Button from "../../../components/common/Button";
import { useAuth } from "../../../contexts/AuthContextProvider";
import { alertT } from "../../../i18n/alerts";

const RecommendPanel = () => {
  const { t } = useTranslation(["pages"]);
  const { userType } = useAuth();
  const navigate = useNavigate();
  const handleClick = () => {
    if (userType === "NOT_AUTHED") alertT("pages.landing.loginRequired");
    else navigate("/onboard/foreigner");
  };
  return (
    <section className="flex flex-row mt-7 p-10 border-[1.5px] border-violet-200 rounded-2xl">
      {recommendations(t).map((rec, idx) => (
        <div key={idx} className="flex flex-col w-85 gap-4">
          <rec.icon />
          {rec.message}
        </div>
      ))}
      <Button variant="primary" size="large" className="px-5 gap-2 ml-auto mt-auto" onClick={handleClick}>
        {t("landing.recommendPanel.registerButton")}
        <IcPencilLine color="white" />
      </Button>
    </section>
  );
};

export default RecommendPanel;

const recommendations = (t: any) => [
  {
    icon: RPMessage,
    message: (
      <>
        {t("landing.recommendPanel.consultMessage")
          .split("\n")
          .map((line: string, idx: number) => (
            <span key={idx}>
              {line}
              {idx < t("landing.recommendPanel.consultMessage").split("\n").length - 1 && <br />}
            </span>
          ))}
      </>
    ),
  },
  {
    icon: RPPeople,
    message: (
      <>
        {t("landing.recommendPanel.withAttorney")
          .split("\n")
          .map((line: string, idx: number) => (
            <span key={idx}>
              {line}
              {idx < t("landing.recommendPanel.withAttorney").split("\n").length - 1 && <br />}
            </span>
          ))}
      </>
    ),
  },
  {
    icon: RPDocument,
    message: (
      <>
        {t("landing.recommendPanel.feedback")
          .split("\n")
          .map((line: string, idx: number) => (
            <span key={idx}>
              {line}
              {idx < t("landing.recommendPanel.feedback").split("\n").length - 1 && <br />}
            </span>
          ))}
      </>
    ),
  },
];
