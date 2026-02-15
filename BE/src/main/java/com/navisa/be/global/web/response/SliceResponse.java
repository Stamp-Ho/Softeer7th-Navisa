package com.navisa.be.global.web.response;

import java.util.List;

public record SliceResponse<T, ID> (
        List<T> content,
        Boolean existsNext,
        ID lastElementId
) {

}
