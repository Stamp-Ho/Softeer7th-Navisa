package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForeignerNationalityRepository extends JpaRepository<ForeignerNationality, Long> {

    @Query("SELECT fn FROM ForeignerNationality fn WHERE fn.foreignerProfile.id = :foreignerId")
    List<ForeignerNationality> findByForeignerProfileId(@Param("foreignerId") UUID foreignerId);
}
