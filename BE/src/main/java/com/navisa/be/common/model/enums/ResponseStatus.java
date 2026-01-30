package com.navisa.be.common.model.enums;

public enum ResponseStatus {
    /**
     * 성공 관련
     */
    SUCCESS(200, "요청에 성공하였습니다."),
    PAGING_SUCCESS(200, "조회 성공"),
    GOOGLE_LOGIN_SUCCESS(200, "구글 로그인 성공"),
    SIGNUP_SUCCESS(200, "회원가입 성공"),
    REISSUE_SUCCESS(200, "토큰 재발급 성공"),
    LOGIN_SUCCESS(200, "로그인 성공"),
    LOGOUT_SUCCESS(200, "로그아웃 성공 및 토큰 무효화 완료"),
    CREATED_FOREIGNER_PROFILE(201, "외국인 프로필 생성에 성공했습니다."),
    UPDATED_FOREIGNER_PROFILE(204, "외국인 프로필 수정(재등록)에 성공했습니다."),

    /**
     * 클라이언트 에러 (400번대)
     */
    BAD_REQUEST(400, "잘못된 요청입니다."),
    INVALID_USER(400, "존재하지 않는 유저 정보입니다."),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    GOOGLE_AUTH_ERROR(401, "구글 인증 서버와의 통신에 실패했습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    USER_INVALID(401, "해당하는 사용자가 존재하지 않습니다."),
    ALREADY_EXIST_USER(409, "이미 가입된 이메일입니다."),
    DUPLICATE_LOGIN_TYPE(409, "다른 로그인 방식(소셜 등)으로 이미 가입된 계정입니다."),
    INVALID_INITIAL_USER_TYPE(400, "가입 시 유효하지 않은 유저 타입입니다."),
    INVALID_FOREIGNER(400, "존재하지 않는 외국인 프로필 정보입니다."),
    INVALID_NATIONALITY(400, "잘못된 Nationality id 정보입니다."),
    INVALID_LANGUAGE(400, "잘못된 Language id 정보입니다."),
    STORAGE_UNSUPPORTED_CONTENT_TYPE(400, "지원하지 않는 파일 타입입니다"),
    STORAGE_UNSUPPORTED_USAGE(400, "지원되지 않는 용도의 요청입니다"),
    AGENT_PROFILE_MUST_CONTAIN_ONE_TYPE_LICENSE_INFO(400, "일반 자격증 정보 세트 혹은 관리번호 중 '하나만' 입력해야 합니다."),
    INVALID_JOB_CODE(400, "유효하지 않은 직무 코드입니다"),
    NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE(400, "행정사 프로필을 등록할 수 있는 유저가 아닙니다"),

    /**
     * 서버 에러 (500번대)
     */
    SERVER_ERROR(500, "서버와의 연결에 실패하였습니다."),
    SIMILARITY_CALCULATE_FAIL(540, "vector의 코사인 유사도 계산을 실패했습니다."),
    PRESIGNED_URL_GEN_FAILED(597, "업로드 URL 생성에 실패했습니다"),
    NOT_FOUND_TEXT_EMBEDDING_RESULT(598, "Text Embedding API를 호출한 결과에 임베딩 결과가 없습니다."),
    CANNOT_GENERATE_TEXT_EMBEDDING_RESULT(599, "Text Embedding API 호출에 실패했습니다.");

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
