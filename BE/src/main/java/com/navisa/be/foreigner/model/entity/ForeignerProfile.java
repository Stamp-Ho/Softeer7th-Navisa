package com.navisa.be.foreigner.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.model.enums.RandomNickname;
import jakarta.persistence.*;
import lombok.Getter;

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

    @Column(name = "profile_object_key")
    private String profileObjectKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ForeignerSearchStatus status;

    @OneToMany(mappedBy = "foreignerProfile")
    private Set<ForeignerLanguage> foreignLanguages = new HashSet<>();

    @OneToMany(mappedBy = "foreignerProfile")
    private Set<ForeignerNationality> foreignerNationalities = new HashSet<>();

    public ForeignerProfile() {
        this.nickname = RandomNickname.getRandomNickname();
    }

    public ForeignerProfile(UUID userId, ForeignerSearchStatus status) {
        this.nickname = RandomNickname.getRandomNickname();
        this.userId = userId;
        this.status = status;
    }

    // 프로필 이미지 업데이트 메서드
    public void updateProfileImage(String profileObjectKey) {
        this.profileObjectKey = profileObjectKey;
    }

    public void updateStatus(ForeignerSearchStatus status) {
        this.status = status;
    }
}
