import { createContext } from "react";

type AuthContextType = {
  userType: feUserType;
  setUserType: (a: feUserType) => void;
  accessToken: string;
  setAccessToken: (a: string) => void;
  getAccessToken: () => string;
  userId: string;
  setUserId: (a: string) => void;
  logOut: () => void;
};

const defaultContext: AuthContextType = {
  userType: "NOT_AUTHED",
  setUserType: () => {},
  accessToken: "",
  setAccessToken: () => {},
  getAccessToken: () => {
    return "";
  },
  userId: "",
  setUserId: () => {},
  logOut: () => {},
};

export const AuthContext = createContext<AuthContextType>(defaultContext);

export type feUserType =
  | "VALID_AGENT"
  | "INVALID_AGENT"
  | "FILLED_FOREIGNER"
  | "UNFILLED_FOREIGNER"
  | "ADMIN"
  | "NOT_AUTHED";
