import { useMutation } from "@tanstack/react-query";
import { useAuth } from "../../contexts/AuthContextProvider";
import useApiClient from "../../hooks/useApiClient";
import { authService } from "../services/auth";
import type { SignupRequest } from "../types/auth";

export const useSignUpMutation = (onSignUpSuccess?: () => void) => {
  const { apiClient } = useApiClient();
  const { setAccessToken, setUserType, setUserId } = useAuth();

  return useMutation({
    mutationFn: (data: SignupRequest) => authService.signup(apiClient, data),
    onSuccess: (res) => {
      setAccessToken(res.result.accessToken);
      setUserType(res.result.userType);
      setUserId(res.result.userId);
      if (onSignUpSuccess) onSignUpSuccess();
    },
    onError: (error) => {
      console.error("회원가입 실패: ", error);
      alert("회원가입에 실패했습니다.");
    },
  });
};
