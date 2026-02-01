package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
