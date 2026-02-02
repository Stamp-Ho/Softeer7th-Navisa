package com.navisa.be.agent.model.enums;

import lombok.Getter;

import java.util.*;

@Getter
public enum OfficeAddressRegion {
    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEJEON("대전"),
    ULSAN("울산"),
    SEJONG("세종"),
    GYEONGGI("경기"),
    GANGWON("강원"),
    GYEONGBUK("경상북도", "경북"),
    GYEONGNAM("경상남도", "경남"),
    JEONBUK("전라북도", "전북"),
    JEONNAM("전라남도", "전남"),
    CHUNGBUK("충청북도", "충북"),
    CHUNGNAM("충청남도", "충남"),
    JEJU("제주");


    private final List<String> aliases;

    private static final Map<String, OfficeAddressRegion> LOOKUP = new HashMap<>();

    static {
        for (OfficeAddressRegion region : values()) {
            for (String alias : region.aliases) {
                LOOKUP.put(alias, region);
            }
        }
    }

    OfficeAddressRegion(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    public static List<String> getAllSearchKeywords(List<String> inputRegions) {
        if (inputRegions == null || inputRegions.isEmpty()) return List.of();

        return inputRegions.stream()
                .map(LOOKUP::get)
                .filter(Objects::nonNull)
                .flatMap(region -> region.aliases.stream()) // 3. 해당 지역의 모든 alias 추출
                .distinct()
                .toList();
    }
}