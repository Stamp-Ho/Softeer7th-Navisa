import { useContext, useEffect, useState } from "react";
import { AuthContext, type feUserType } from "./AuthContext";

export const AuthContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [userType, setUserType] = useState<feUserType>(() => {
    const savedUserType = localStorage.getItem("userType");
    return savedUserType ? JSON.parse(savedUserType) : null;
  });
  const [userId, setUserId] = useState<string>(() => {
    const savedUserId = localStorage.getItem("userId");
    return savedUserId ? JSON.parse(savedUserId) : null;
  });
  const [accessToken, setAccessToken] = useState<string>("");

  const logOut = () => {
    setAccessToken("");
    setUserId("");
    setUserType("NOT_AUTHED");
  };

  // 2. 유저 상태가 바뀔 때마다 로컬 스토리지 업데이트
  useEffect(() => {
    if (userType) {
      localStorage.setItem("userType", JSON.stringify(userType));
    } else {
      localStorage.removeItem("userType");
    }
  }, [userType]);
  useEffect(() => {
    if (userId) {
      localStorage.setItem("userId", JSON.stringify(userId));
    } else {
      localStorage.removeItem("userId");
    }
  }, [userId]);

  return (
    <AuthContext.Provider
      value={{
        userType,
        setUserType,
        accessToken,
        setAccessToken,
        userId,
        setUserId,
        logOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
