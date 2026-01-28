package com.navisa.be.auth.dto;

import com.navisa.be.user.model.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.") String email,
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.") String password,
        @NotNull(message = "사용자 타입(행정사/외국인) 선택은 필수입니다.") UserType userType
) {}