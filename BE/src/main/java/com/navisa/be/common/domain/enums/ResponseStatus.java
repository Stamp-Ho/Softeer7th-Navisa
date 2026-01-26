package com.navisa.be.common.domain.enums;

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
    SERVER_ERROR(false, 500, "서버와의 연결에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    ResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    // 직접 작성한 Getter (JaCoCo가 인식하기 좋습니다)
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