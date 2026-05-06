import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

// 한국어 번역 파일들 import
import koCommon from "./locales/ko/common.json";
import koPages from "./locales/ko/pages.json";
import koComponents from "./locales/ko/components.json";
import koUtils from "./locales/ko/utils.json";

// 영어 번역 파일들 import
import enCommon from "./locales/en/common.json";
import enPages from "./locales/en/pages.json";
import enComponents from "./locales/en/components.json";
import enUtils from "./locales/en/utils.json";

// 중국어 번역 파일들 import
import chCommon from "./locales/ch/common.json";
import chPages from "./locales/ch/pages.json";
import chComponents from "./locales/ch/components.json";
import chUtils from "./locales/ch/utils.json";

// 일본어 번역 파일들 import
import jaCommon from "./locales/ja/common.json";
import jaPages from "./locales/ja/pages.json";
import jaComponents from "./locales/ja/components.json";
import jaUtils from "./locales/ja/utils.json";

// 번역 리소스 정의
const resources = {
  ko: {
    common: koCommon,
    pages: koPages,
    components: koComponents,
    utils: koUtils,
  },
  en: {
    common: enCommon,
    pages: enPages,
    components: enComponents,
    utils: enUtils,
  },
  ch: {
    common: chCommon,
    pages: chPages,
    components: chComponents,
    utils: chUtils,
  },
  ja: {
    common: jaCommon,
    pages: jaPages,
    components: jaComponents,
    utils: jaUtils,
  },
};

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: "ko",
    ns: ["common", "pages", "components", "utils"],
    defaultNS: "common",
    interpolation: {
      escapeValue: false,
    },
    detection: {
      order: ["localStorage", "navigator"],
      caches: ["localStorage"],
    },
  });

export default i18n;
