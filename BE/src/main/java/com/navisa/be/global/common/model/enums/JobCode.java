package com.navisa.be.global.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum JobCode {

    ECONOMIC_OR_PROFIT_ORG_EXECUTIVE("S110", "경제이익단체 고위임원"),
    CORPORATE_EXECUTIVE("1120", "기업 고위임원"),
    BUSINESS_SUPPORT_MANAGER("121", "경영지원 관리자"),
    EDUCATION_MANAGER("1312", "교육 관리자"),
    INSURANCE_FINANCE_MANAGER("1320", "보험 및 금융관리자"),
    CULTURE_ART_DESIGN_VIDEO_MANAGER("1340", "문화·예술·디자인 및 영상관련 관리자"),
    IT_MANAGER("1350", "정보통신관련 관리자"),
    PROFESSIONAL_SERVICE_MANAGER("1390", "기타 전문서비스 관리자"),
    CONSTRUCTION_MINING_MANAGER("1411", "건설 및 광업 관련 관리자"),
    PRODUCTION_MANAGER("1413", "제품 생산관련 관리자"),
    AGRICULTURE_FORESTRY_FISHERY_MANAGER("1490", "농림·어업관련 관리자"),
    SALES_MARKETING_MANAGER("1511", "영업 및 판매 관련 관리자"),
    TRANSPORTATION_MANAGER("1512", "운송관련 관리자"),
    HOSPITALITY_SPORTS_MANAGER("1521", "숙박·여행·오락 및 스포츠 관련 관리자"),
    FOOD_SERVICE_MANAGER("1522", "음식서비스관련 관리자"),
    LIFE_SCIENCE_EXPERT("2111", "생명과학 전문가"),
    NATURAL_SCIENCE_EXPERT("2112", "자연과학 전문가"),
    SOCIAL_SCIENCE_RESEARCHER("2122", "사회과학 연구원"),
    COMPUTER_HARDWARE_ENGINEER("2211", "컴퓨터 하드웨어 기술자"),
    COMMUNICATION_ENGINEER("2212", "통신공학 기술자"),
    SYSTEM_DESIGNER_ANALYST("2221", "컴퓨터시스템 설계 및 분석가"),
    SYSTEM_SOFTWARE_DEVELOPER("2222", "시스템 소프트웨어 개발자"),
    APPLICATION_SOFTWARE_DEVELOPER("2223", "응용 소프트웨어 개발자"),
    WEB_DEVELOPER("2224", "웹 개발자"),
    DATA_EXPERT("2231", "데이터 전문가"),
    NETWORK_DEVELOPER("2232", "네트워크시스템 개발자"),
    INFO_SECURITY_EXPERT("2233", "정보 보안 전문가"),
    ARCHITECT("2311", "건축가"),
    ARCHITECTURAL_ENGINEER("2312", "건축공학 기술자"),
    CIVIL_ENGINEER("2313", "토목공학 전문가"),
    LANDSCAPE_ARCHITECT("2314", "조경 기술자"),
    URBAN_TRANSPORT_EXPERT("2315", "도시 및 교통관련 전문가"),
    CHEMICAL_ENGINEER("2321", "화학공학 기술자"),
    METAL_MATERIAL_ENGINEER("2331", "금속·재료 공학 기술자"),
    ELECTRICAL_ENGINEER("2341", "전기공학 기술자"),
    ELECTRONIC_ENGINEER("2342", "전자공학 기술자"),
    MECHANICAL_ENGINEER("2351", "기계공학 기술자"),
    PLANT_ENGINEER("23512", "플랜트공학 기술자"),
    ROBOTIC_EXPERT("2352", "로봇공학 전문가"),
    VEHICLE_ENGINEER("S2353", "자동차·조선·비행기·철도차량공학 전문가"),
    SAFETY_RISK_EXPERT("2364", "산업안전 및 위험 전문가"),
    ENVIRONMENTAL_ENGINEER("2371", "환경공학 기술자"),
    GAS_ENERGY_ENGINEER("2372", "가스·에너지 기술자"),
    TEXTILE_ENGINEER("2392", "섬유공학 기술자"),
    DRAFTSMAN("2395", "제도사"),
    NURSE("2430", "간호사"),
    UNIVERSITY_LECTURER("2512", "대학 강사"),
    TECHNICAL_SCHOOL_LECTURER("2543", "해외기술전문학교 기술강사"),
    EDUCATION_EXPERT("2591", "교육관련 전문가"),
    FOREIGN_SCHOOL_TEACHER("2599", "외국인학교·외국교육기관·국제학교·영재학교 등의 교사"),
    LEGAL_EXPERT("261", "법률 전문가"),
    ADMIN_EXPERT("2620", "정부 및 공공 행정 전문가"),
    SPECIAL_ORG_ADMIN("S2620", "특수기관 행정요원"),
    MANAGEMENT_EXPERT("271", "경영 및 진단 전문가"),
    FINANCE_INSURANCE_EXPERT("272", "금융 및 보험 전문가"),
    PRODUCT_PLANNER("2731", "상품기획 전문가"),
    TRAVEL_PRODUCT_DEVELOPER("2732", "여행상품 개발자"),
    PR_EXPERT("2733", "광고 및 홍보 전문가"),
    RESEARCH_EXPERT("2734", "조사 전문가"),
    EVENT_PLANNER("2735", "행사 기획자"),
    OVERSEAS_SALES("2742", "해외 영업원"),
    TECHNICAL_SALES("2743", "기술 영업원"),
    TECH_MANAGEMENT_EXPERT("S2743", "기술경영 전문가"),
    TRANSLATOR_INTERPRETER("2814", "번역가·통역가"),
    ANNOUNCER("28331", "아나운서"),
    DESIGNER("285", "디자이너"),
    VIDEO_DESIGNER("S2855", "영상관련 디자이너"),
    DUTY_FREE_SHOP_CLERK("31215", "면세점 또는 제주영어교육도시 내 판매 사무원"),
    AIR_TRANSPORT_CLERK("31264", "항공운송 사무원"),
    HOTEL_RECEPTIONIST("3922", "호텔 접수 사무원"),
    MEDICAL_COORDINATOR("S3922", "의료 코디네이터"),
    CUSTOMER_COUNSELOR("3991", "고객상담 사무원"),
    TRANSPORT_SERVICE_WORKER("431", "운송 서비스 종사자"),
    TOUR_INTERPRETER_GUIDE("43213", "관광 통역 안내원"),
    CASINO_DEALER("43291", "카지노 딜러"),
    CHEF_COOK("441", "주방장 및 조리사"),
    ANIMAL_BREEDER("61395", "동물 사육사"),
    AQUACULTURE_TECHNICIAN("6301", "양식기술자"),
    HALAL_BUTCHER("7103", "할랄 도축원"),
    INSTRUMENT_MAKER_TUNER("7303", "악기제조 및 조율사"),
    SHIP_WELDER("7430", "조선용접공"),
    AIRCRAFT_MAINTENANCE("7521", "항공기 정비원"),
    SHIP_PAINTER("78369", "선박 도장공"),
    SHIP_ELECTRICIAN("76212", "선박 전기원"),
    ROOT_INDUSTRY_SKILLED_WORKER("S740", "뿌리산업체 숙련기능공"),
    AGRI_FORESTRY_FISHERY_SKILLED_WORKER("S610", "농림축산어업 숙련기능인"),
    MANUFACTURING_CONSTRUCTION_SKILLED_WORKER("S700", "일반 제조업체 및 건설업체 숙련기능공");

    private final String code;
    private final String name;

    public static JobCode findByCode(String code) {
        return Arrays.stream(JobCode.values())
                .filter(jc -> jc.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}