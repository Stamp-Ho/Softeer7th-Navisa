package com.navisa.be.common.repository;

import com.navisa.be.common.model.entity.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {
}
