import { useState } from "react";
import { LocaleContext } from "./LocaleContext";

export const LocaleContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [locale, setLocale] = useState<"ko" | "ch" | "en" | "ja">("ko");

  return (
    <LocaleContext.Provider value={{ locale, setLocale }}>
      {children}
    </LocaleContext.Provider>
  );
};
