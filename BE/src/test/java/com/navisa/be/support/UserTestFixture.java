package com.navisa.be.support;

import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class UserTestFixture {

    private final UserRepository userRepository;

    public User createUser(String email, UserType userType) {
        User user = new User(
                email,
                "hash",
                userType,
                LoginType.EMAIL,
                true);

        return userRepository.save(user);
    }
}
