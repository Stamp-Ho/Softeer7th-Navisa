package com.navisa.be.admin.service;

import com.navisa.be.admin.dto.request.PermitNewAgentRequest;
import com.navisa.be.admin.exception.AdminDomainException;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("존재하지 않는 유저라면 permitNewAgent는 예외를 일으킨다")
    @Test
    void permitNewAgent_shouldThrowException_whenUserNotFound(){
        // given
        UUID userId = UUID.randomUUID();
        PermitNewAgentRequest request = new PermitNewAgentRequest(userId);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.permitNewAgent(request))
                .isInstanceOf(AdminDomainException.class);

    }

    @DisplayName("유저가 인증되지 않은 행정사가 아니면 permitNewAgent은 예외를 일으킨다")
    @Test
    void permitNewAgent_shouldThrowException_whenUserIsNotInvalidAgent(){
        // given
        UUID userId = UUID.randomUUID();
        PermitNewAgentRequest request = new PermitNewAgentRequest(userId);

        User user = new User("email", "hash", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> adminService.permitNewAgent(request))
                .isInstanceOf(AdminDomainException.class);
    }

    @DisplayName("permitAllAgent는 행정사 인증처리에 성공한다")
    @Test
    void permitNewAgent_shouldSucceed(){
        // given
        UUID userId = UUID.randomUUID();
        PermitNewAgentRequest request = new PermitNewAgentRequest(userId);

        User user = mock(User.class);
        when(user.getUserType()).thenReturn(UserType.UNVALID_AGENT);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // when
        adminService.permitNewAgent(request);

        // then
        Mockito.verify(user, times(1)).upgradeToValidAgent();
    }
}