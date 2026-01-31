package com.navisa.be.storage.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public enum StorageLocation {
    AGENT_PROFILE_IMAGE("agent-profile", "agent-profile"),
    FOREIGNER_IDENTITY_IMAGE("foreigner-identity", "foreigner-identity");

    private final String usage;
    @Getter
    private final String directory;

    private static final Map<String, StorageLocation> LOCATION_MAP = Collections.unmodifiableMap(
            Arrays.stream(values()).collect(Collectors.toMap(
                    location -> location.usage,
                    location -> location,
                    (existing, replacement) -> existing
            ))
    );

    StorageLocation(String usage, String directory) {
        this.usage = usage;
        this.directory = directory;
    }

    public static boolean supportsUsage(String fileUsage) {
        if(fileUsage == null || fileUsage.isBlank()){
            return false;
        }
        return LOCATION_MAP.containsKey(fileUsage);
    }

    public static StorageLocation from(String fileUsage) {
        if(fileUsage == null || fileUsage.isBlank()){
            throw new IllegalArgumentException("용도는 비어 있을 수 없습니다");
        }

        StorageLocation location = LOCATION_MAP.get(fileUsage);
        if(location == null){
            throw new IllegalArgumentException("지원하지 않는 용도입니다");
        }
        return location;
    }

}
