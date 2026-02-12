/**
 * UTC 시간을 사용자의 로컬 시간 형식으로 변환
 * @param dateString - '2026-02-12T08:26:55Z' 같은 ISO 또는 UTC 포맷
 */
export const formatToLocalTime = (dateString: string | Date): string => {
  if (!dateString) return "-";
  const dateWithZ = String(dateString).endsWith("Z")
    ? dateString
    : dateString + "Z";
  const date = new Date(dateWithZ);

  // 브라우저 설정에 따른 로컬 시간 포맷팅
  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false, // 24시간 형식
  }).format(date);
};
