package com.navisa.be.common.dto.request;

public record SliceRequest<ID> (
        ID lastElementId,
        Integer size
) {
}
