package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForeignerEducationRepository extends JpaRepository<ForeignerEducation, Long> {

    Optional<ForeignerEducation> findByForeignerId(UUID foreignerId);
}
