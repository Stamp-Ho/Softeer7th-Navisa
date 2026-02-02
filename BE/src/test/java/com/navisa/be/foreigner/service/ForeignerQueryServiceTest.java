package com.navisa.be.foreigner.service;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("userId로 외국인 전체 정보를 조회한다")
    void findForeignerTotalInfo() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));
        User savedUser = userRepository.save(User.createGoogleUser("query@example.com", UserType.UNFILLED_FOREIGNER));
        UUID userId = savedUser.getId();
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

    @Test
    @DisplayName("이메일로 외국인 상세 요건 입력 상태를 조회한다.")
    void checkForeignerFilledStatus() {
        // given
        Language language = languageRepository.save(new Language(null, "Korean"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "Vietnam"));

        String email = "test@navisa.com";
        User user = userRepository.save(User.createGoogleUser(email, UserType.FILLED_FOREIGNER));

        UUID userId = user.getId(); // 저장된 유저의 ID 추출

        // 2. 해당 userId를 사용하여 외국인 프로필 등록
        ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                List.of(nationality.getId()),
                List.of(language.getId()),
                true);

        foreignerCommandService.registerForeignerTotalInfo(request, userId);

        em.flush();
        em.clear();

        // when
        ForeignerStatusResponse response = foreignerQueryService.checkForeignerFilledStatus(email);

        // then
        assertThat(response).isNotNull();
        assertThat(response.foreignerProfileId()).isNotNull();
        assertThat(response.isCompletedRecommendation()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 상태 조회 시 예외가 발생한다.")
    void checkForeignerFilledStatus_UserNotFound() {
        // given
        String nonExistentEmail = "none@navisa.com";

        // when & then
        assertThatThrownBy(() -> foreignerQueryService.checkForeignerFilledStatus(nonExistentEmail))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.USER_INVALID);
    }
}
