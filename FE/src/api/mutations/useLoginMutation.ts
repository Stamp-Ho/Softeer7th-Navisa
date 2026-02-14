import { useMutation } from "@tanstack/react-query";
import { authService } from "../services/auth";
import useApiClient from "../../hooks/useApiClient";
import type { LoginRequest } from "../types/auth";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useLoginMutation = (onLoginSuccess?: () => void) => {
  const { apiClient } = useApiClient();
  const { setAccessToken, setUserType, setUserId } = useAuth();

  return useMutation({
    mutationFn: (data: LoginRequest) => authService.login(apiClient, data),
    onSuccess: (res) => {
      setAccessToken(res.result.accessToken);
      setUserType(res.result.userType);
      setUserId(res.result.userId);

      if (onLoginSuccess) onLoginSuccess();
    },
    onError: (error) => {
      console.error("로그인 실패: ", error);
      alert("이메일 혹은 비밀번호를 확인해주세요.");
    },
  });
};
