package com.navisa.be.common.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(
        List<T> content,
        PageInfo pageInfo
) {
    public record PageInfo(
            int pageNum,
            int pageSize,
            long totalElements,
            int totalPages
    ) {}

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new PageInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }
}
