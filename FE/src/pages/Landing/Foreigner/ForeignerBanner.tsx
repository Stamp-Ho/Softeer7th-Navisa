import NavisaLogo from "../../../assets/NavisaLogo";
import { useTranslation } from "react-i18next";

const ForeignerBanner = () => {
  const { t } = useTranslation(["pages"]);

  return (
    <div className="flex flex-col gap-9 my-50">
      <NavisaLogo height={12} />
      <div className="flex flex-col gap-3">
        <a className="banner-title">{t("landing.foreignerBanner")}</a>
        <a className="headline-m-medium text-gray-600">{t("landing.foreignerDescription")}</a>
      </div>
    </div>
  );
};

export default ForeignerBanner;
