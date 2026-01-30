package com.navisa.be.common.model.entity;

import com.navisa.be.common.model.enums.BadgeName;
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
    @Column(name = "agent_badge_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_name")
    private BadgeName badgeName;

    public Badge(BadgeName badgeName) {
        this.badgeName = badgeName;
    }
}