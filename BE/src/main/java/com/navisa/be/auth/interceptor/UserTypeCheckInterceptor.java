package com.navisa.be.auth.interceptor;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.model.enums.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class UserTypeCheckInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // HandlerMethod가 아니면 통과
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 컨트롤러 메서드에 어노테이션이 없으면 통과
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        HasUserType hasUserType = handlerMethod.getMethodAnnotation(HasUserType.class);
        if (hasUserType == null) {
            return true;
        }

        // 요청에서 이메일을 추출하고 사용자가 역할을 가지는지 확인
        String email = (String) request.getAttribute("email");
        if(email == null || email.isBlank()){
            throw new AuthException(ResponseStatus.FORBIDDEN);
        }

        // 사용자의 역할과 어노테이션에 명시된 역할이 다르면 예외 발생
        if(!authService.checkUserType(email, hasUserType.value())){
            throw new AuthException(ResponseStatus.FORBIDDEN);
        }

        return true;
    }
}
