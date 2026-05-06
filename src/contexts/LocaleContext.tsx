import { createContext } from "react";

type LocaleContextType = {
  locale: Language;
  setLocale: (lang: Language) => void;
};

export const LocaleContext = createContext<LocaleContextType | null>(null);

type Language = "ko" | "ch" | "en" | "ja";
