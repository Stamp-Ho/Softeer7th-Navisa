import { useMutation } from "@tanstack/react-query";
import useApiClient from "../../hooks/useApiClient";
import { agentService } from "../services/agent";
import { foreignerService } from "../services/foreigner";

export const useBadgeReviewMutation = () => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: { badgeIdList: number[]; agentId: string }) => {
      return agentService.postBadgeReview(apiClient, data);
    },
  });
};

export const useFeedbackReviewMutation = () => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: { content: string }) => {
      return agentService.postFeedbackReview(apiClient, data);
    },
  });
};

export const useAgentStatusFinishedMutation = (formId: string) => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: (data: { isFinished: boolean }) => {
      return agentService.patchAgentStatusFinished(apiClient, formId, data);
    },
  });
};

export const useForeignerStatusFinishedMutation = () => {
  const { apiClient } = useApiClient();

  return useMutation({
    mutationFn: () => {
      return foreignerService.patchForeignerStatusFinished(apiClient);
    },
  });
};
