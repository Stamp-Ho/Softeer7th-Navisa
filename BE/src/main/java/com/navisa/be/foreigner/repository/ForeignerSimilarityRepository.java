package com.navisa.be.foreigner.repository;

import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForeignerSimilarityRepository extends JpaRepository<ForeignerSimilarity, Long> {

    Optional<ForeignerSimilarity> findByForeignerId(UUID foreignerId);

    void deleteByForeignerId(UUID foreignerId);
}
