package com.navisa.be.storage.model.enums;

import lombok.Getter;

@Getter
public enum ImageSize {
    ORIGIN("origin"),
    MEDIUM("resize/medium"),
    SMALL("resize/small");

    private final String path;

    ImageSize(String path) { this.path = path; }
}
