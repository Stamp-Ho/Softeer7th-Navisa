package com.navisa.be.foreigner.model.enums;

import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public enum RandomNickname {
    SPRING_HAECHEE("봄날의해치"),
    COOL_DOLHARUBANG("시원한돌하르방"),
    HAPPY_GUMDO("행복한검도인"),
    CLEVER_MAGPIE("똑똑한까치"),
    WARM_ONDOL("따뜻한온돌"),
    SHINING_NAMSAN("빛나는남산"),
    BRAVE_TIGER("용감한호랑이"),
    SWEET_PERSIMMON("달콤한홍시"),
    BLUE_DONGHAE("푸른동해"),
    STRONG_PINE("듬직한소나무");

    private final String value;

    RandomNickname(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getRandomNickname() {
        RandomNickname[] values = RandomNickname.values();
        int nicknameIndex = ThreadLocalRandom.current().nextInt(values.length);
        String baseName = values[nicknameIndex].getValue();

        int randomNumber = ThreadLocalRandom.current().nextInt(1000);
        String suffix = String.format("%03d", randomNumber);

        return baseName + " " + suffix; // 예: "봄날의해치 042"
    }
}