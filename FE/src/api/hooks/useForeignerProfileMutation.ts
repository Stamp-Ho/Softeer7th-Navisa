import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { foreignerService } from "../services/foreigner";
import type { ForeignerRegisterRequest } from "../types/foreigner";
import { useAuth } from "../../contexts/AuthContextProvider";

export const useForiengerProfileMutation = (onSuccess?: () => void) => {
  const { apiClient } = useApiClient();
  const { accessToken, setUserType } = useAuth();

  return useMutation({
    mutationFn: (data: ForeignerRegisterRequest) =>
      foreignerService.updateProfile(apiClient, data, accessToken),
    onSuccess: () => {
      if (onSuccess) onSuccess();
      setUserType("FILLED_FOREIGNER");
    },
    onError: (error) => {
      console.error("요건 등록 실패: ", error);
      alert("내 요건 등록에 실패했습니다.");
    },
  });
};
