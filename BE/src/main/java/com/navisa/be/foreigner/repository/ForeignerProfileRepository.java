package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForeignerProfileRepository extends JpaRepository<ForeignerProfile, UUID> {

    @Query("SELECT p FROM ForeignerProfile p " +
            "LEFT JOIN FETCH p.foreignerNationalities fn " +
            "LEFT JOIN FETCH fn.nationality " +
            "LEFT JOIN FETCH p.foreignLanguages fl " +
            "LEFT JOIN FETCH fl.language " +
            "WHERE p.userId = :userId")
    Optional<ForeignerProfile> findByUserIdWithNationalitiesAndLanguages(@Param("userId") UUID userId);

    @Query("SELECT fp FROM ForeignerProfile fp WHERE fp.userId = :userId")
    Optional<ForeignerProfile> findByUserId(@Param("userId") UUID userId);
}
