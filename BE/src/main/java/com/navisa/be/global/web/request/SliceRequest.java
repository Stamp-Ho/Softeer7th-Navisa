package com.navisa.be.global.web.request;

public record SliceRequest<ID> (
        ID lastElementId,
        Integer size
) {
}
