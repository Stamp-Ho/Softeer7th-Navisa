package com.navisa.be.global.web.request;

import io.swagger.v3.oas.annotations.Parameter;

public record SliceRequest<ID>(
        @Parameter(description = "마지막으로 조회한 요소의 ID (첫 페이지는 null 또는 생략)") ID lastElementId,
        @Parameter(description = "페이지 크기 (기본값은 엔드포인트마다 다름)") Integer size) {
}
