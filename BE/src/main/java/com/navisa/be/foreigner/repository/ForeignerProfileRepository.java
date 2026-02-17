package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.querydsl.ForeignerProfileQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForeignerProfileRepository extends JpaRepository<ForeignerProfile, UUID>, ForeignerProfileQueryDsl {

    @Query("SELECT fp FROM ForeignerProfile fp WHERE fp.userId = :userId")
    Optional<ForeignerProfile> findByUserId(@Param("userId") UUID userId);
}
