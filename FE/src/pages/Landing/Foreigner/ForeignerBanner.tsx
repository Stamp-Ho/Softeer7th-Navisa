import NavisaLogo from "../../../assets/NavisaLogo";
import { useTranslation } from "react-i18next";

const ForeignerBanner = () => {
  const { t } = useTranslation(["pages"]);

  return (
    <div className="flex flex-col gap-9 my-50">
      <NavisaLogo height={12} />
      <div className="flex flex-col gap-3">
        <h2 className="banner-title">{t("landing.foreignerBanner")}</h2>
        <p className="headline-m-medium text-gray-600">{t("landing.foreignerDescription")}</p>
      </div>
    </div>
  );
};

export default ForeignerBanner;
