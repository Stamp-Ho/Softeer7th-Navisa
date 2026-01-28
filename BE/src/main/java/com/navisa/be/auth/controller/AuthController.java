package com.navisa.be.auth.controller;

import com.navisa.be.auth.dto.*;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.common.model.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 구글 로그인
    @PostMapping("/oauth/google")
    public BaseResponse<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest loginRequest) {
        LoginResponse response = authService.googleLogin(loginRequest);
        return new BaseResponse<>(ResponseStatus.GOOGLE_LOGIN_SUCCESS.getMessage(), response);
    }

    // 일반 회원가입
    @PostMapping("/signup")
    public BaseResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return new BaseResponse<>(ResponseStatus.SIGNUP_SUCCESS.getMessage(), response);
    }

    // 일반 로그인
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return new BaseResponse<>(ResponseStatus.LOGIN_SUCCESS.getMessage(), response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public BaseResponse<String> logout(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody LogoutRequest request
    ) {
        authService.logout(accessToken, request);
        return new BaseResponse<>(ResponseStatus.LOGOUT_SUCCESS.getMessage());
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        TokenResponse response = authService.reissue(request.refreshToken());
        return new BaseResponse<>(ResponseStatus.REISSUE_SUCCESS.getMessage(), response);
    }
}
