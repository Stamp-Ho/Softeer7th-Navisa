package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface ForeignerLanguageRepository extends JpaRepository<ForeignerLanguage, Long> {

    @Query("SELECT fl FROM ForeignerLanguage fl WHERE fl.foreignerProfile.id = :foreignerId")
    List<ForeignerLanguage> findByForeignerProfileId(@Param("foreignerId") UUID foreignerId);
}
