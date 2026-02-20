import { useContext, useState } from "react";
import { useTranslation } from "react-i18next";
import FlagIcon from "../../assets/FlagIcon";
import { IcArrows } from "../../assets/icon/StratisUi";
import { LocaleContext } from "../../contexts/LocaleContext";

type Language = "ko" | "ch" | "en" | "ja";

interface LanguageOption {
  code: Language;
  flagIndex: number;
  labelKey: string;
  label: string;
}

const LanguageSelector = () => {
  const { t } = useTranslation(["common"]);
  const localeContext = useContext(LocaleContext);
  const [isOpen, setIsOpen] = useState(false);

  const flagStyle = "w-8 h-8 border border-gray-200 rounded-full";

  const languages: LanguageOption[] = [
    { code: "ko", flagIndex: 25, labelKey: "languageSelector.ko", label: "KO" },
    { code: "ch", flagIndex: 7, labelKey: "languageSelector.ch", label: "ZH" },
    { code: "en", flagIndex: 61, labelKey: "languageSelector.en", label: "EN" },
    { code: "ja", flagIndex: 22, labelKey: "languageSelector.ja", label: "JA" },
  ];

  const currentLang = languages.find((lang) => lang.code === localeContext?.locale);

  const handleLanguageChange = (code: Language) => {
    if (localeContext) {
      localeContext.setLocale(code);
    }
    setIsOpen(false);
  };

  return (
    <div
      className="z-30 border border-gray-200 cursor-pointer rounded-3xl bg-white h-fit p-2 pr-3 flex flex-col gap-3"
      data-testid="language-selector"
    >
      <div
        className="flex flex-row items-center gap-2 hover:opacity-70 transition-opacity"
        onClick={() => setIsOpen(!isOpen)}
        title={t("languageSelector.title")}
      >
        <FlagIcon nationIndex={currentLang?.flagIndex || 25} className={flagStyle} />
        <div className={`transition-transform duration-200 ${isOpen ? "rotate-180" : ""}`}>
          <IcArrows />
        </div>
      </div>
      {isOpen && (
        <div className="flex flex-col gap-3">
          {languages.map(
            (lang) =>
              lang.code !== localeContext?.locale && (
                <div
                  key={lang.code}
                  className="flex flex-row items-center gap-3 caption-l-bold text-gray-700 cursor-pointer opacity-70 hover:opacity-100 transition-opacity"
                  onClick={() => handleLanguageChange(lang.code)}
                >
                  <FlagIcon
                    nationIndex={lang.flagIndex}
                    className={flagStyle}
                    title={t(lang.labelKey)}
                    data-testid={`lang-${lang.code}`}
                  />
                  {lang.label}
                </div>
              ),
          )}
        </div>
      )}
    </div>
  );
};

export default LanguageSelector;
