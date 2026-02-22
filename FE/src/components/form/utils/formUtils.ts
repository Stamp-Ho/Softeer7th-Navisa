export const calculateOnlyInputs = (data: any) => {
  let totalCount = 0;
  let filledCount = 0;

  const traverse = (obj: any) => {
    if (obj === null || obj === undefined) return;

    // 최종 값(배열의 요소)에 도달했을 때
    if (typeof obj !== "object") {
      totalCount++;
      if (obj < 0) return;
      const strValue = String(obj).trim();
      if (strValue !== "") filledCount++;
      return;
    }

    // 객체나 배열이면 계속 탐색 (단, disabled 필드는 제외)
    Object.entries(obj).forEach(([key, value]) => {
      if (key !== "disabled") {
        traverse(value);
      }
    });
  };

  traverse(data);
  return { totalCount, filledCount };
};

export const getLeafValues = (obj: any) => {
  let result: any = [];

  const traverse = (target: any) => {
    if (target === null || target === undefined) return;

    // 1. 원시 타입(값) 발견 시 추가
    if (typeof target !== "object") {
      if (String(target).trim() !== "") result.push(target + 1);
      return;
    }

    // 2. 객체/배열 순회 (메타데이터 제외)
    Object.entries(target).forEach(([key, value]) => {
      // disabled나 rowId 등 시스템용 키는 제외
      if (key.includes("disabled") || key === "rowId") return;
      traverse(value);
    });
  };

  traverse(obj);
  return result;
};
