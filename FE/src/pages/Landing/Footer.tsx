import { useTranslation } from "react-i18next";
import FlagIcon from "../../assets/FlagIcon";
import NavisaLogo from "../../assets/NavisaLogo";

const Footer = () => {
  const { t } = useTranslation(["pages"]);
  const currentYear = new Date().getFullYear();

  return (
    <footer className="w-480 -ml-50 mt-25 bg-slate-800 text-slate-300 pt-12 pb-8 px-6">
      <div className="max-w-7xl mx-auto">
        {/* 상단: 서비스 로고 및 국가 퀵 링크 */}
        <div className="flex flex-col lg:flex-row justify-between mb-6 border-b border-gray-800 pb-10 gap-10">
          <div className="max-w-sm">
            <NavisaLogo whiteMode={true} />
            <h2 className="text-2xl font-bold text-white mt-1 mb-4 tracking-tight">{t("landing.brandName")}</h2>
            <p className="text-sm leading-relaxed">
              {t("landing.tagline")} <br />
              <strong>Navisa</strong> {t("landing.subtitle")}
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-8 text-sm">
            <div>
              <h4 className="text-white font-bold mb-4">{t("landing.service")}</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">{t("landing.aboutVisa")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.visaProcedure")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.eVisa")}</li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">{t("landing.category.attorney")}</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">{t("landing.category.attorneyTerms")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.category.attorneyInquiry")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.category.ourAttorneys")}</li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">{t("landing.support.title")}</h4>
              <ul className="space-y-2">
                <li className="hover:text-white cursor-pointer transition">{t("landing.support.faq")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.support.embassyGuide")}</li>
                <li className="hover:text-white cursor-pointer transition">{t("landing.support.inquiry")}</li>
              </ul>
            </div>
          </div>
        </div>

        {/* 하단: 저작권 및 글로벌 네트워크 (FlagIcon 활용) */}
        <div className="flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="text-xs space-y-1">
            <p>{t("landing.footer.copyright", { year: currentYear })}</p>
            <div className="flex gap-4">
              <span className="hover:underline cursor-pointer">{t("landing.footer.terms")}</span>
              <span className="hover:underline cursor-pointer text-white font-medium">{t("landing.footer.privacy")}</span>
            </div>
          </div>

          {/* 주요 서비스 국가 아이콘 노출 */}
          <div className="flex items-center gap-2 bg-gray-800/50 px-4 py-2 rounded-full border border-gray-700">
            <span className="text-[10px] uppercase font-bold tracking-widest mr-2">{t("landing.footer.topDestinations")}</span>
            <div className="flex -space-x-2">
              {[25, 7, 61, 22].map((idx) => (
                <div key={idx} className="w-6 h-6 rounded-full border-2 border-[#111827] overflow-hidden">
                  <FlagIcon nationIndex={idx} className="w-full h-full object-cover shadow-sm" />
                </div>
              ))}
            </div>
            <span className="text-xs ml-2 text-gray-300">{t("landing.footer.countries")}</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
