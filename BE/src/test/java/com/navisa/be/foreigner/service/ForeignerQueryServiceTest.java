package com.navisa.be.foreigner.service;

import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ForeignerQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerQueryService foreignerQueryService;

    @Autowired
    private ForeignerCommandService foreignerCommandService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("userId로 외국인 전체 정보를 조회한다")
    void findForeignerTotalInfo() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));
        UUID userId = UUID.randomUUID();
        boolean isWork = false;

        ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                List.of(nationality.getId()),
                List.of(language.getId()),
                isWork);

        // Save data using Command Service
        foreignerCommandService.registerForeignerTotalInfo(request, userId);

        em.flush();
        em.clear();

        // when
        ForeignerQueryResponse response = foreignerQueryService.findForeignerTotalInfo(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isIdle()).isEqualTo(request.isIdle());

        assertThat(response.education()).usingRecursiveComparison().isEqualTo(request.education());

        assertThat(response.expectedCompany()).usingRecursiveComparison().isEqualTo(request.expectedCompany());

        assertThat(response.foreignerCareers()).hasSize(1);
        assertThat(response.foreignerCareers().get(0)).usingRecursiveComparison()
                .isEqualTo(request.foreignerCareers().get(0));

        assertThat(response.languageIdList()).containsExactly(language.getId());
        assertThat(response.nationIdList()).containsExactly(nationality.getId());
    }
}
