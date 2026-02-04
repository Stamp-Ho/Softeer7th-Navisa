package com.navisa.be.foreigner.dto.request;

import java.util.UUID;

public record FindForeignerDetailCommand(
        String loginUserEmail,
        UUID foreignerId
) {
}
