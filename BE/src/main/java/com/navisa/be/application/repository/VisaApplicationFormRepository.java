package com.navisa.be.application.repository;

import com.navisa.be.application.model.entity.VisaApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisaApplicationFormRepository extends JpaRepository<VisaApplicationForm, UUID> {
}
