import { createContext } from "react";

type AuthContextType = {
  userType: feUserType;
  setUserType: (a: feUserType) => void;
  accessToken: string;
  setAccessToken: (a: string) => void;
};

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);

export type feUserType =
  | "VALID_AGENT"
  | "UNVALID_AGENT"
  | "FILLED_FOREIGNER"
  | "UNFILLED_FOREIGNER"
  | "ADMIN"
  | "NOT_AUTHED";
