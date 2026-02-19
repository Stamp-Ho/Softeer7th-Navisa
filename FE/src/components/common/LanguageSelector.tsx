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
}

const LanguageSelector = () => {
  const { t } = useTranslation(["common"]);
  const localeContext = useContext(LocaleContext);
  const [isOpen, setIsOpen] = useState(false);

  const flagStyle = "w-8 h-8 border border-gray-200 rounded-full cursor-pointer hover:opacity-70 transition-opacity";

  const languages: LanguageOption[] = [
    { code: "ko", flagIndex: 25, labelKey: "languageSelector.ko" },
    { code: "ch", flagIndex: 7, labelKey: "languageSelector.ch" },
    { code: "en", flagIndex: 61, labelKey: "languageSelector.en" },
    { code: "ja", flagIndex: 22, labelKey: "languageSelector.ja" },
  ];

  const currentLang = languages.find((lang) => lang.code === localeContext?.locale);

  const handleLanguageChange = (code: Language) => {
    if (localeContext) {
      localeContext.setLocale(code);
    }
    setIsOpen(false);
  };

  return (
    <div className="z-30 border border-gray-200 cursor-pointer rounded-3xl bg-white h-fit p-2 pr-4 flex flex-col gap-3" data-testid="language-selector">
      <div className="flex flex-row items-center gap-2 hover:opacity-70 transition-opacity" onClick={() => setIsOpen(!isOpen)} title={t("languageSelector.title")}>
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
                <FlagIcon key={lang.code} nationIndex={lang.flagIndex} className={flagStyle} onClick={() => handleLanguageChange(lang.code)} title={t(lang.labelKey)} data-testid={`lang-${lang.code}`} />
              ),
          )}
        </div>
      )}
    </div>
  );
};

export default LanguageSelector;
