package com.navisa.be.global.common.repository;

import com.navisa.be.global.common.model.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    long countByIdIn(List<Long> ids);
    List<Language> findAllByOrderByIdAsc();
}
