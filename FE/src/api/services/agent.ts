import type { apiClientType, FetchOptions } from "../../hooks/useApiClient";
import type { BaseResponse } from "../types/common";
import * as T from "../types/agent";

type AgentRequestOptions = Pick<FetchOptions, "headers">;

export const agentService = {
  recommendedAgents: (api: apiClientType, options?: AgentRequestOptions) =>
    api.get<BaseResponse<T.AgentCardResponse[]>>(
      "/api/home/guest/agents",
      undefined,
      options,
    ),
};
