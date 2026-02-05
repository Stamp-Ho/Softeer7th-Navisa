import { useContext, useState } from "react";
import { AuthContext, type feUserType } from "./AuthContext";

export const AuthContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [userType, setUserType] = useState<feUserType>("NOT_AUTHED");
  const [accessToken, setAccessToken] = useState<string>("");
  const [userId, setUserId] = useState<string>("");

  return (
    <AuthContext.Provider
      value={{
        userType,
        setUserType,
        accessToken,
        setAccessToken,
        userId,
        setUserId,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
