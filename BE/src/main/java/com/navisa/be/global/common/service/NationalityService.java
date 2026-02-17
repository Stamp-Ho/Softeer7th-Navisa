package com.navisa.be.global.common.service;

import com.navisa.be.global.common.dto.response.NationalityListResponse;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.repository.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NationalityService {

    private final NationalityRepository nationalityRepository;

    @Transactional(readOnly = true)
    public List<Nationality> findAllById(List<Long> nationalityIds) {
        return nationalityRepository.findAllById(nationalityIds);
    }

    @Transactional(readOnly = true)
    public long countByIdIn(List<Long> nationalityIds) {
        return nationalityRepository.countByIdIn(nationalityIds);
    }
}
