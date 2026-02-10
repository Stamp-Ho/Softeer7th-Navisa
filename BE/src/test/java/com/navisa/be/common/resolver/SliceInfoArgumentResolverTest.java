package com.navisa.be.common.resolver;

import com.navisa.be.common.annotation.SliceInfo;
import com.navisa.be.common.dto.request.SliceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SliceInfoArgumentResolverTest {

    @InjectMocks
    private SliceInfoArgumentResolver resolver;

    @Mock
    private MethodParameter parameter;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private WebDataBinderFactory binderFactory;

    @BeforeEach
    void setUp() {
        // 기본적으로 SliceInfo 어노테이션이 존재하는 것으로 설정 (size=16)
        // 개별 테스트에서 다른 값이 필요하면 override 가능 (lenient 필요할 수 있음)
        SliceInfo defaultSliceInfo = mock(SliceInfo.class);
        org.mockito.Mockito.lenient().when(defaultSliceInfo.size()).thenReturn(16);
        org.mockito.Mockito.lenient().when(parameter.getParameterAnnotation(SliceInfo.class))
                .thenReturn(defaultSliceInfo);
    }

    @Test
    @DisplayName("supportsParameter는 @SliceInfo가 있고 타입이 SliceRequest여야 true를 반환한다")
    void supportsParameter_shouldReturnTrue_whenConditionMet() {
        // given
        when(parameter.hasParameterAnnotation(SliceInfo.class)).thenReturn(true);
        doReturn(SliceRequest.class).when(parameter).getParameterType();

        // when
        boolean result = resolver.supportsParameter(parameter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("supportsParameter는 조건 만족 안하면 false 반환")
    void supportsParameter_shouldReturnFalse_whenAnnotationMissing() {
        // given
        when(parameter.hasParameterAnnotation(SliceInfo.class)).thenReturn(false);

        // when
        boolean result = resolver.supportsParameter(parameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("resolveArgument는 기본 size를 16으로 설정하고, lastElementId가 없으면 null로 설정한다")
    void resolveArgument_shouldReturnDefault_whenNoParams() {
        // given
        when(webRequest.getParameter("lastElementId")).thenReturn(null);
        when(webRequest.getParameter("size")).thenReturn(null);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        assertThat(result).isInstanceOf(SliceRequest.class);
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.size()).isEqualTo(16);
        assertThat(request.lastElementId()).isNull();
    }

    @Test
    @DisplayName("resolveArgument는 size 파라미터가 있으면 해당 값을 사용한다")
    void resolveArgument_shouldUseProvidedSize() {
        // given
        when(webRequest.getParameter("lastElementId")).thenReturn(null);
        when(webRequest.getParameter("size")).thenReturn("20");

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("resolveArgument는 size 파라미터가 없고 어노테이션에 size가 설정되어 있으면 그 값을 사용한다")
    void resolveArgument_shouldUseAnnotationSize() {
        // given
        when(webRequest.getParameter("lastElementId")).thenReturn(null);
        when(webRequest.getParameter("size")).thenReturn(null);

        SliceInfo sliceInfo = mock(SliceInfo.class);
        when(sliceInfo.size()).thenReturn(50);
        when(parameter.getParameterAnnotation(SliceInfo.class)).thenReturn(sliceInfo);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.size()).isEqualTo(50);
    }

    @Test
    @DisplayName("lastElementId가 Long 타입일 때 올바르게 파싱한다")
    void resolveArgument_shouldParseLongId() {
        // given
        String idStr = "123";
        when(webRequest.getParameter("lastElementId")).thenReturn(idStr);
        when(webRequest.getParameter("size")).thenReturn(null);

        mockGenericType(Long.class);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.lastElementId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("lastElementId가 UUID 타입일 때 올바르게 파싱한다")
    void resolveArgument_shouldParseUuidId() {
        // given
        UUID uuid = UUID.randomUUID();
        String idStr = uuid.toString();
        when(webRequest.getParameter("lastElementId")).thenReturn(idStr);
        when(webRequest.getParameter("size")).thenReturn(null);

        mockGenericType(UUID.class);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.lastElementId()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("lastElementId가 Integer 타입일 때 올바르게 파싱한다")
    void resolveArgument_shouldParseIntegerId() {
        // given
        String idStr = "10";
        when(webRequest.getParameter("lastElementId")).thenReturn(idStr);
        when(webRequest.getParameter("size")).thenReturn(null);

        mockGenericType(Integer.class);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.lastElementId()).isEqualTo(10);
    }

    @Test
    @DisplayName("lastElementId가 String 타입(기본)일 때 올바르게 파싱한다")
    void resolveArgument_shouldUseStringAsDefault() {
        // given
        String idStr = "some-string-id";
        when(webRequest.getParameter("lastElementId")).thenReturn(idStr);
        when(webRequest.getParameter("size")).thenReturn(null);

        mockGenericType(String.class);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // then
        SliceRequest<?> request = (SliceRequest<?>) result;
        assertNotNull(request);
        assertThat(request.lastElementId()).isEqualTo("some-string-id");
    }

    // Helper method to mock generic type
    private void mockGenericType(Class<?> genericType) {
        ParameterizedType mockedType = mock(ParameterizedType.class);
        when(parameter.getGenericParameterType()).thenReturn(mockedType);
        when(mockedType.getActualTypeArguments()).thenReturn(new Type[] { genericType });
    }
}
