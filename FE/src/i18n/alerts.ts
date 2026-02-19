import i18n from "./config";

/**
 * 다국어 alert 래퍼 함수
 * @param key - 번역 키 (예: 'components.form.savingSuccess')
 * @param params - 동적 파라미터 (예: { name: 'John' })
 */
export const alertT = (key: string, params?: Record<string, any>) => {
  const message = i18n.t(key, params);
  alert(message);
};

/**
 * 다국어 confirm 래퍼 함수
 * @param key - 번역 키
 * @param params - 동적 파라미터
 * @returns confirm 결과 (true/false)
 */
export const confirmT = (key: string, params?: Record<string, any>) => {
  const message = i18n.t(key, params);
  return confirm(message);
};
