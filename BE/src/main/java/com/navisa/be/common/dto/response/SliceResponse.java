package com.navisa.be.common.dto.response;

import java.util.List;

public record SliceResponse<T, ID> (
        List<T> content,
        Boolean existsNext,
        ID lastElementId
) {

}
