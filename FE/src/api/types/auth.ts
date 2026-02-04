import type { UserType } from "./common";

export interface SignupRequest {
  email: string;
  password: string;
  userType: UserType;
}

export interface SignupResponse {
  accessToken: string;
  userId: string;
  userType: UserType;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  userId: string;
}

export interface GoogleLoginRequest {
  idToken: string;
  userType: UserType;
}

export interface TokenResponse {
  accessToken: string;
}
