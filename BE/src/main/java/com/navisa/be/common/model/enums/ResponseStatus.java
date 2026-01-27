package com.navisa.be.common.model.enums;

public enum ResponseStatus {
    /**
     * 성공 관련
     */
    SUCCESS(true, 200, "요청에 성공하였습니다."),
    PAGING_SUCCESS(true, 200, "조회 성공"),

    /**
     * 클라이언트 에러 (400번대)
     */
    BAD_REQUEST(false, 400, "잘못된 요청입니다."),
    INVALID_USER(false, 400, "존재하지 않는 유저 정보입니다."),

    /**
     * 서버 에러 (500번대)
     */
    SERVER_ERROR(false, 500, "서버와의 연결에 실패하였습니다."),

    NOT_FOUND_TEXT_EMBEDDING_RESULT(false, 598, "Text Embedding API를 호출한 결과에 임베딩 결과가 없습니다."),
    CANNOT_GENERATE_TEXT_EMBEDDING_RESULT(false, 599, "Text Embedding API 호출에 실패했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    ResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
