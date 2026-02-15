package com.navisa.be.agent.model.entity;

import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.global.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_name")
    private BadgeName badgeName;

    public Badge(BadgeName badgeName) {
        this.badgeName = badgeName;
    }
}