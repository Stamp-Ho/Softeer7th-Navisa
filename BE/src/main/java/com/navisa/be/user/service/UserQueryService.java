package com.navisa.be.user.service;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ResponseStatus.INVALID_USER));
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseStatus.INVALID_USER));
    }
}
