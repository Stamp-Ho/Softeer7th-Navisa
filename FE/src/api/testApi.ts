import { useQuery } from "@tanstack/react-query";
import useApiClient from "./useApiClient";

const URL = "/api/info/languages";
const accessToken =
  "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmZVRlc3QwMDAxQGV4YW1wbGUuY29tIiwiaWF0IjoxNzcwMTY0MTEwLCJleHAiOjE3NzAxNjc3MTB9.FQXaa7aIgVQC9KDD9Ce2AQvMbxLCVbNJoto831CeNVQ_QWS1xFCwFG1ih8jmFpqLV2YoF9dRjmN_2XpcGQy-3g";

export const getLanguageList = () => {
  const { apiClient } = useApiClient();

  // queryKey에 URL 변수를 그대로 사용합니다.
  return useQuery({
    queryKey: [URL],
    queryFn: async () => {
      const res = await apiClient.get(URL, {
        headers: {
          Authorization: `Bearer ${accessToken}`, // 오타 수정
        },
      });
      return res;
    },
  });
};
