package com.navisa.be.foreigner.dto.request;

import java.util.UUID;

public record ForeignerDetailRequest(
        String loginUserEmail,
        UUID foreignerId
) {
}
