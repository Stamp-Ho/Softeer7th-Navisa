package com.navisa.be.global.common.repository;

import com.navisa.be.global.common.model.entity.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {

    List<Nationality> findAllByOrderByIdAsc();

    long countByIdIn(List<Long> nationalityIds);
}
