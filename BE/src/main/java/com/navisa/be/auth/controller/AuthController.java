package com.navisa.be.auth.controller;

import com.navisa.be.auth.dto.request.GoogleLoginRequest;
import com.navisa.be.auth.dto.request.LoginRequest;
import com.navisa.be.auth.dto.request.SignupRequest;
import com.navisa.be.auth.dto.response.LoginResponse;
import com.navisa.be.auth.dto.response.SignupResponse;
import com.navisa.be.auth.dto.response.TokenResponse;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.common.model.enums.ResponseStatus;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    // 구글 로그인
    @Operation(summary = "구글 로그인", description = "구글 OAuth 로그인을 수행하고 토큰을 발급받습니다.")
    @PostMapping("/oauth/google")
    public BaseResponse<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.googleLogin(loginRequest, response);
        return new BaseResponse<>(ResponseStatus.GOOGLE_LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    // 일반 회원가입
    @Operation(summary = "일반 회원가입", description = "일반 이메일 회원가입을 수행합니다.")
    @PostMapping("/signup")
    public BaseResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletResponse response) {
        SignupResponse signupResponse = authService.signup(request, response);
        return new BaseResponse<>(ResponseStatus.SIGNUP_SUCCESS.getMessage(), signupResponse);
    }

    // 일반 로그인
    @Operation(summary = "일반 로그인", description = "일반 이메일 로그인을 수행하고 토큰을 발급받습니다.")
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, response);
        return new BaseResponse<>(ResponseStatus.LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 수행합니다.")
    @PostMapping("/logout")
    public BaseResponse<String> logout(@RequestHeader("Authorization") String accessToken,
                                       HttpServletResponse response) {
        authService.logout(accessToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new BaseResponse<>(ResponseStatus.LOGOUT_SUCCESS.getMessage());
    }

    // 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 Access Token을 재발급받습니다.")
    @PostMapping("/reissue")
    public BaseResponse<TokenResponse> reissue(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.reissue(refreshToken, response);
        return new BaseResponse<>(ResponseStatus.REISSUE_SUCCESS.getMessage(), tokenResponse);
    }
}
