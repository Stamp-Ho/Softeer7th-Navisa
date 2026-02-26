import { useTranslation } from "react-i18next";
import { nationList, nationByCategory } from "../constants/nations";
import type { DegreeLevel } from "../api/types/common";

/** regionList의 국제화 라벨 */
export function useRegionLabels(): string[] {
  const { t } = useTranslation(["common"]);
  const regionKeys = [
    "regions.seoul",
    "regions.busan",
    "regions.daegu",
    "regions.incheon",
    "regions.gwangju",
    "regions.daejeon",
    "regions.ulsan",
    "regions.sejong",
    "regions.gyeonggiDo",
    "regions.gangwonDo",
    "regions.northChungcheong",
    "regions.southChungcheong",
    "regions.northJeolla",
    "regions.southJeolla",
    "regions.northGyeongsang",
    "regions.southGyeongsang",
    "regions.jeju",
  ];
  return regionKeys.map((key) => t(key));
}

/** nationList의 국제화 라벨 */
export function useNationLabels(): string[] {
  const { t } = useTranslation(["common"]);
  const nationKeys = [
    "nations.algeria",
    "nations.argentina",
    "nations.australia",
    "nations.bangladesh",
    "nations.brazil",
    "nations.canada",
    "nations.chile",
    "nations.china",
    "nations.colombia",
    "nations.denmark",
    "nations.egypt",
    "nations.ethiopia",
    "nations.finland",
    "nations.france",
    "nations.germany",
    "nations.ghana",
    "nations.hongkong",
    "nations.india",
    "nations.iran",
    "nations.iraq",
    "nations.israel",
    "nations.italy",
    "nations.japan",
    "nations.kazakhstan",
    "nations.kenya",
    "nations.korea",
    "nations.kuwait",
    "nations.kyrgyzstan",
    "nations.mexico",
    "nations.mongolia",
    "nations.morocco",
    "nations.nepal",
    "nations.netherlands",
    "nations.newzealand",
    "nations.nigeria",
    "nations.norway",
    "nations.pakistan",
    "nations.peru",
    "nations.qatar",
    "nations.russia",
    "nations.saudiarabia",
    "nations.southafrica",
    "nations.cambodia",
    "nations.indonesia",
    "nations.laos",
    "nations.malaysia",
    "nations.myanmar",
    "nations.philippines",
    "nations.singapore",
    "nations.thailand",
    "nations.vietnam",
    "nations.spain",
    "nations.srilanka",
    "nations.sweden",
    "nations.tajikistan",
    "nations.unitedkingdom",
    "nations.taiwan",
    "nations.tunisia",
    "nations.turkey",
    "nations.ukraine",
    "nations.uae",
    "nations.usa",
    "nations.uzbekistan",
  ];
  return nationKeys.map((key) => t(key));
}

/** languageList의 국제화 라벨 */
export function useLanguageLabels(): string[] {
  const { t } = useTranslation(["common"]);
  const languageKeys = [
    "languages.korean",
    "languages.english",
    "languages.chinese",
    "languages.japanese",
    "languages.vietnamese",
    "languages.thai",
    "languages.russian",
    "languages.french",
    "languages.german",
    "languages.spanish",
    "languages.italian",
    "languages.uzbek",
    "languages.filipino",
    "languages.nepali",
    "languages.indonesian",
    "languages.other",
  ];
  return languageKeys.map((key) => t(key));
}

/** 학위 레벨의 국제화 맵 */
export function useDegreeLabelMap(): Record<DegreeLevel, string> {
  const { t } = useTranslation(["common"]);
  return {
    BELOW_BACHELOR: t("degrees.BELOW_BACHELOR"),
    BACHELOR: t("degrees.BACHELOR"),
    ABOVE_MASTER: t("degrees.ABOVE_MASTER"),
  };
}

/** jobCodeList의 국제화 라벨 */
export function useJobCodeLabels(): string[] {
  const { t } = useTranslation(["common"]);
  const jobCodeKeys = Array.from({ length: 87 }, (_, i) => `jobCodes.${i}`);
  return jobCodeKeys.map((key) => t(key));
}

/** 배지 라벨의 국제화 라벨 */
export function useBadgeLabels(): string[] {
  const { t } = useTranslation(["common"]);
  const badgeKeys = Array.from({ length: 15 }, (_, i) => `badges.${i}`);
  return badgeKeys.map((key) => t(key));
}

/** nationByCategory의 국제화 버전 */
export function useNationByCategory(): {
  name: string;
  items: string[];
}[] {
  const { t } = useTranslation(["common"]);
  const nationLabels = useNationLabels();

  // 카테고리명을 i18n으로 변환
  const categoryNameMap: Record<string, string> = {
    동아시아: t("nationCategories.eastAsia"),
    동남아시아: t("nationCategories.southeastAsia"),
    남아시아: t("nationCategories.southAsia"),
    중앙아시아: t("nationCategories.centralAsia"),
    중동: t("nationCategories.middleEast"),
    유럽: t("nationCategories.europe"),
    아프리카: t("nationCategories.africa"),
    아메리카: t("nationCategories.america"),
    오세아니아: t("nationCategories.oceania"),
  };

  // nationByCategory의 각 카테고리에서 국가명을 한국어 → i18n 라벨로 변환
  return nationByCategory.map((category) => ({
    name: categoryNameMap[category.name] || category.name,
    items: category.items.map((koreanName) => {
      const idx = nationList.indexOf(koreanName);
      return idx !== -1 ? nationLabels[idx] : koreanName;
    }),
  }));
}
