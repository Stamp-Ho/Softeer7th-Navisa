package com.navisa.be.user.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.exception.UserException;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    @Getter
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public User(String email, String passwordHash, UserType userType, LoginType loginType, Boolean isVerified) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.loginType = loginType;
        this.isVerified = isVerified;
    }

    public static User createGoogleUser(String email, UserType userType) {
        return new User(
                email,
                null,
                userType,
                LoginType.GOOGLE,
                true
        );
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void upgradeToValidAgent() {
        if(this.userType != UserType.UNVALID_AGENT){
            throw new UserException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE);
        }
        this.userType = UserType.VALID_AGENT;
    }

    public void upgradeToValidForeigner() {
        if(this.userType != UserType.UNFILLED_FOREIGNER){
            throw new UserException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_FOREIGNER_PROFILE);
        }
        this.userType = UserType.FILLED_FOREIGNER;
    }
}
