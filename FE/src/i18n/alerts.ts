import i18n from "./config";

/**
 * 다국어 alert 래퍼 함수
 * @param key - 번역 키 (예: 'pages.documents.savingSuccess' 또는 'documents.savingSuccess')
 * @param params - 동적 파라미터 (예: { name: 'John' })
 */
export const alertT = (key: string, params?: Record<string, any>) => {
  // key가 namespace를 포함한 형식인 경우 (예: 'pages.documents.savingSuccess')
  let message = i18n.t(key, params);

  // 키값이 그대로 반환되면, 첫 번째 부분을 namespace로 분리 시도
  if (message === key && key.includes(".")) {
    const parts = key.split(".");
    const namespace = parts[0];
    const keyPath = parts.slice(1).join(".");
    message = i18n.t(keyPath, { ns: namespace, ...params });
  }

  alert(message);
};

/**
 * 다국어 confirm 래퍼 함수
 * @param key - 번역 키 (예: 'pages.documents.confirmDelete' 또는 'documents.confirmDelete')
 * @param params - 동적 파라미터
 * @returns confirm 결과 (true/false)
 */
export const confirmT = (key: string, params?: Record<string, any>) => {
  // key가 namespace를 포함한 형식인 경우 (예: 'pages.documents.confirmDelete')
  let message = i18n.t(key, params);

  // 키값이 그대로 반환되면, 첫 번째 부분을 namespace로 분리 시도
  if (message === key && key.includes(".")) {
    const parts = key.split(".");
    const namespace = parts[0];
    const keyPath = parts.slice(1).join(".");
    message = i18n.t(keyPath, { ns: namespace, ...params });
  }

  return confirm(message);
};
