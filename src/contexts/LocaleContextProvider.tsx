import { useEffect, useState } from "react";
import { LocaleContext } from "./LocaleContext";
import i18n from "../i18n/config";

type Language = "ko" | "ch" | "en" | "ja";

export const LocaleContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [locale, setLocaleState] = useState<Language>("ko");
  const [isReady, setIsReady] = useState(false);

  // 앱 시작 시 localStorage에서 언어 복원 또는 브라우저 설정 사용
  useEffect(() => {
    const savedLocale = localStorage.getItem("locale") as Language | null;

    if (savedLocale && ["ko", "ch", "en", "ja"].includes(savedLocale)) {
      setLocaleState(savedLocale);
      i18n.changeLanguage(savedLocale);
    } else {
      // 브라우저 언어 감지 (i18next LanguageDetector 사용)
      // 지원 언어: ko, en, ch, ja
      const browserLang = navigator.language.split("-")[0];
      const supportedLangs: Language[] = ["ko", "ch", "en", "ja"];
      const detectedLang = (supportedLangs.includes(browserLang as Language) ? browserLang : "ko") as Language;

      setLocaleState(detectedLang);
      i18n.changeLanguage(detectedLang);
      localStorage.setItem("locale", detectedLang);
    }

    setIsReady(true);
  }, []);

  const setLocale = (lang: Language) => {
    setLocaleState(lang);
    i18n.changeLanguage(lang);
    localStorage.setItem("locale", lang);
  };

  // i18n이 준비될 때까지 기다린 후 렌더링
  if (!isReady) {
    return null;
  }

  return <LocaleContext.Provider value={{ locale, setLocale }}>{children}</LocaleContext.Provider>;
};
