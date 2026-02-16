import { useContext, useEffect, useState } from "react";
import { AuthContext, type feUserType } from "./AuthContext";
import { useNavigate } from "react-router-dom";
import useApiClient from "../hooks/useApiClient";

export const AuthContextProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const { refreshAccessToken } = useApiClient();
  const navigate = useNavigate();
  const [initialLized, setInitialized] = useState(false);
  const [userType, setUserType] = useState<feUserType>(() => {
    const savedUserType = localStorage.getItem("userType");
    return savedUserType ? JSON.parse(savedUserType) : null;
  });
  const [userId, setUserId] = useState<string>(() => {
    const savedUserId = localStorage.getItem("userId");
    return savedUserId ? JSON.parse(savedUserId) : null;
  });
  const [accessToken, setAccessToken] = useState<string>("");

  const getAccessToken = () => {
    return accessToken;
  };
  const logOut = () => {
    setAccessToken("");
    setUserId("");
    setUserType("NOT_AUTHED");
    navigate("/");
    localStorage.clear();
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

  useEffect(() => {
    const initAuth = async () => {
      try {
        // Reissue API를 호출해서 메모리에 토큰을 다시 채움
        const newToken = await refreshAccessToken();
        setAccessToken(newToken);
        setInitialized(true);
      } catch (e) {
        console.log("로그인 필요 상태");
      }
    };
    initAuth();
  }, []);
  if (!initialLized) return <></>;
  return (
    <AuthContext.Provider
      value={{
        userType,
        setUserType,
        accessToken,
        setAccessToken,
        getAccessToken,
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
