package com.navisa.be.foreigner.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.model.enums.RandomNickname;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "foreigner_profile")
@Entity
@Getter
public class ForeignerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "foreigner_id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ForeignerSearchStatus status;

    @OneToMany(mappedBy = "foreignerProfile")
    private Set<ForeignerLanguage> foreignLanguages = new HashSet<>();

    @OneToMany(mappedBy = "foreignerProfile")
    private Set<ForeignerNationality> foreignerNationalities = new HashSet<>();

    @Column(name = "last_login_at")
    private ZonedDateTime lastLoginAt;

    public ForeignerProfile() {
        this.nickname = RandomNickname.getRandomNickname();
    }

    public ForeignerProfile(UUID userId, ForeignerSearchStatus status) {
        this.nickname = RandomNickname.getRandomNickname();
        this.userId = userId;
        this.status = status;
    }

    public ForeignerProfile(UUID userId, String nickname, ForeignerSearchStatus status) {
        this.nickname = nickname;
        this.userId = userId;
        this.status = status;
    }

    public void updateStatus(ForeignerSearchStatus status) {
        this.status = status;
    }

    public void updateLastLogin() { this.lastLoginAt = ZonedDateTime.now(); }
}
