package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForeignerExpectedCompanyRepository extends JpaRepository<ForeignerExpectedCompany, Long> {

    Optional<ForeignerExpectedCompany> findByForeignerId(UUID foreignerId);
}
