package com.navisa.be.auth.resolver;

import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginUserResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) &&
                parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 어노테이션의 required 속성값 가져오기
        LoginUser annotation = parameter.getParameterAnnotation(LoginUser.class);
        boolean isRequired = annotation != null && annotation.required();

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String email = (String) request.getAttribute("email");

        // 이메일이 없는데 필수(required=true)인 경우 예외 발생
        if (email == null && isRequired) {
            throw new BaseException(ResponseStatus.INVALID_TOKEN);
        }

        // 비로그인 상태(email == null)이더라도 required=false면 null 그대로 반환
        return email;
    }
}
