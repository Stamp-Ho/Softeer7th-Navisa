package com.navisa.be.admin.service;

import com.navisa.be.admin.dto.request.AgentPermitRequest;
import com.navisa.be.admin.exception.AdminDomainException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void permitNewAgent(AgentPermitRequest request) {
        // 사용자가 존재하지 않으면 예외 발생
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new AdminDomainException(ResponseStatus.INVALID_USER));

        // 행정사 승인 처리
        user.upgradeToValidAgent();
    }
}
