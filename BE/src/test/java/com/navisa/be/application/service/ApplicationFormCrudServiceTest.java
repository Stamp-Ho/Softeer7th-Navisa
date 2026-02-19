package com.navisa.be.application.service;

import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ApplicationFormCrudServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationFormCrudService applicationFormCrudService;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Test
    @DisplayName("ForeignerProfile로 초기 ApplicationForm을 생성하고 저장한다")
    void createInitForm() {
        // given
        User foreignerUser = userTestFixture.createUser("app_test@example.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        // when
        ApplicationForm savedForm = applicationFormCrudService.createInitForm(foreignerProfile);

        // then
        assertThat(savedForm).isNotNull();
        assertThat(savedForm.getId()).isNotNull();
        assertThat(savedForm.getForeignerProfile().getId()).isEqualTo(foreignerProfile.getId());

        // Repository에서 다시 조회하여 검증
        ApplicationForm foundForm = applicationFormRepository.findById(savedForm.getId()).orElse(null);
        assertThat(foundForm).isNotNull();
        assertThat(foundForm.getForeignerProfile().getId()).isEqualTo(foreignerProfile.getId());
        assertThat(foundForm.isDone()).isFalse(); // 초기 상태 확인 (기본값)
    }
}
