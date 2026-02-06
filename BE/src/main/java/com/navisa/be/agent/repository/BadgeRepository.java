package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findAllByIdIn(List<Long> badgeIds);
}
