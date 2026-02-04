package com.navisa.be.common.config;

import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.common.resolver.LoginUserResolver;
import com.navisa.be.common.resolver.SliceInfoArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String ALL_APIS = "/api/**";
    private static final String[] AUTH_EXCLUDED_LIST = {
            "/api/auth/oauth/google",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/reissue",
            "/api/home/badge-list",
            "/api/home/feedback",
            "/api/home/badge",
            "/api/home/guest/agents"
    };
    private final UserTypeCheckInterceptor userTypeCheckInterceptor;
    private final AuthInterceptor authInterceptor;
    private final LoginUserResolver loginUserResolver;
    private final SliceInfoArgumentResolver sliceInfoArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174", "https://api.navisa.site", "https://www.navisa.site")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(ALL_APIS)
                .excludePathPatterns(AUTH_EXCLUDED_LIST);

        // authInterceptor 다음에 이 인터셉터가 실행되어야 함
        registry.addInterceptor(userTypeCheckInterceptor)
                .addPathPatterns(ALL_APIS)
                .excludePathPatterns(AUTH_EXCLUDED_LIST);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserResolver);
        resolvers.add(sliceInfoArgumentResolver);
    }
}
