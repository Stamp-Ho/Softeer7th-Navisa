package com.navisa.be.common.resolver;

import com.navisa.be.common.annotation.SliceInfo;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

@Component
public class SliceInfoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SliceInfo.class) &&
                parameter.getParameterType().equals(SliceRequest.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String lastIdStr = webRequest.getParameter("lastElementId");
        String sizeStr = webRequest.getParameter("size");

        // 1. 사이즈 처리
        Integer size = (sizeStr != null) ? Integer.parseInt(sizeStr) : 16;

        // 2. 제네릭 타입(ID) 확인 및 파싱
        Object lastElementId = null;
        if (lastIdStr != null) {
            // SliceRequest<ID>에서 ID가 무엇인지 알아냄
            try {
                ParameterizedType type = (ParameterizedType) parameter.getGenericParameterType();
                Class<?> idClass = (Class<?>) type.getActualTypeArguments()[0];

                lastElementId = parseId(lastIdStr, idClass);
            } catch (IllegalArgumentException | ClassCastException e) {
                throw new BaseException(ResponseStatus.BAD_REQUEST);
            }
        }

        return new SliceRequest<>(lastElementId, size);
    }

    private Object parseId(String lastIdStr, Class<?> idClass) {
        if (idClass.equals(UUID.class)) {
            return UUID.fromString(lastIdStr);
        } else if (idClass.equals(Long.class)) {
            return Long.parseLong(lastIdStr);
        } else if (idClass.equals(Integer.class)) {
            return Integer.parseInt(lastIdStr);
        }
        return lastIdStr; // 기본적으로 String 반환
    }
}
