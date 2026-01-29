package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForeignerCareersRepository extends JpaRepository<ForeignerCareers, Long> {

    List<ForeignerCareers> findByForeignerId(UUID foreignerId);
}
