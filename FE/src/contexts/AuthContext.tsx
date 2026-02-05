import { createContext } from "react";

type AuthContextType = {
  userType: feUserType;
  setUserType: (a: feUserType) => void;
  accessToken: string;
  setAccessToken: (a: string) => void;
  userId: string;
  setUserId: (a: string) => void;
};

const defaultContext: AuthContextType = {
  userType: "NOT_AUTHED",
  setUserType: () => {},
  accessToken: "",
  setAccessToken: () => {},
  userId: "",
  setUserId: () => {},
};

export const AuthContext = createContext<AuthContextType>(defaultContext);

export type feUserType =
  | "VALID_AGENT"
  | "UNVALID_AGENT"
  | "FILLED_FOREIGNER"
  | "UNFILLED_FOREIGNER"
  | "ADMIN"
  | "NOT_AUTHED";
