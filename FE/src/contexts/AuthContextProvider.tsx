import { useState } from "react";
import { AuthContext, type feUserType } from "./AuthContext";

export const AuthContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [userType, setUserType] = useState<feUserType>("NOT_AUTHED");
  const [accessToken, setAccessToken] = useState<string>("");

  return (
    <AuthContext.Provider
      value={{ userType, setUserType, accessToken, setAccessToken }}
    >
      {children}
    </AuthContext.Provider>
  );
};
