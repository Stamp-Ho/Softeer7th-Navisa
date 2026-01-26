package com.navisa.be.common.dto.response;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@AllArgsConstructor
public class PageResponse<T> {

    public final List<T> content;
    public final PageInfo pageInfo;

    @AllArgsConstructor
    public static class PageInfo {
        public final int pageNum;
        public final int pageSize;
        public final long totalElements;
        public final int totalPages;
    }

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