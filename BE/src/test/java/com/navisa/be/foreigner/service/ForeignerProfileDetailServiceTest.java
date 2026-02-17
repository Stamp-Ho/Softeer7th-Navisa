package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.foreigner.dto.request.ForeignerDetailRequest;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerDetailResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.ForeignerLanguage;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.global.common.repository.NationalityRepository;
import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class ForeignerProfileDetailServiceTest extends IntegrationTestSupport {

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private ForeignerNationalityRepository foreignerNationalityRepository;

    @Autowired
    private ForeignerLanguageRepository foreignerLanguageRepository;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileCrudService foreignerProfileCrudService;

    @Autowired
    private ForeignerProfileDetailService foreignerProfileDetailService;

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

        foreignerProfileCrudService.registerForeignerTotalInfo(
                request,
                userId,
                List.of(language),
                List.of(nationality)
        );

        em.flush();
        em.clear();

        // when
        ForeignerQueryResponse response = foreignerProfileDetailService.findForeignerTotalInfo(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRequesting()).isEqualTo(request.isRequesting());

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

        foreignerProfileCrudService.registerForeignerTotalInfo(
                request,
                userId,
                List.of(language),
                List.of(nationality)
        );

        em.flush();
        em.clear();

        // when
        ForeignerStatusResponse response = foreignerProfileDetailService.checkForeignerFilledStatus(email);

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
        assertThatThrownBy(() -> foreignerProfileDetailService.checkForeignerFilledStatus(nonExistentEmail))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.INVALID_USER);
    }

    @Test
    @DisplayName("외국인 상세 조회에 성공한다")
    void findForeignerDetail_shouldSucceed() {
        // given
        User agentUser = userTestFixture.createUser("agent@user", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("박행정", "주소", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@user", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        foreignerProfile.updateLastLogin();
        foreignerProfileRepository.saveAndFlush(foreignerProfile);

        ForeignerDetailRequest command = new ForeignerDetailRequest(agentUser.getEmail(), foreignerProfile.getId());

        // when
        ForeignerDetailResponse response = foreignerProfileDetailService.findForeignerDetail(command);

        // then
        List<ForeignerNationality> fns = foreignerNationalityRepository.findByForeignerProfileId(foreignerProfile.getId());
        List<ForeignerLanguage> fls = foreignerLanguageRepository.findByForeignerProfileId(foreignerProfile.getId());

        // basicInfo 검증
        assertThat(response.basicInfo().nationIdList()).containsExactlyInAnyOrderElementsOf(
                fns.stream().map(fn -> fn.getNationality().getId()).toList()
        );

        // educationInfo 검증
        assertThat(response.educationInfo().degreeLevel()).isEqualTo(EducationDegreeLevel.ABOVE_MASTER);

        // careerInfo 검증
        assertThat(response.careerInfo().totalCareerMonths()).isEqualTo(12);

        // languageList 검증
        assertThat(response.languageList()).containsExactlyInAnyOrderElementsOf(
                fls.stream().map(foreignerLanguage -> foreignerLanguage.getLanguage().getId()).toList()
        );

        // expectedCompanyInfo 검증
        assertThat(response.expectedCompanyInfo().companyName()).isEqualTo("새 회사");
    }

    @Test
    @DisplayName("외국인 상세 조회 시에 외국인이 없으면 예외가 발생한다")
    void findForeignerDetail_shouldThrowException() {
        // given
        User agentUser = userTestFixture.createUser("agent@user", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("박행정", "주소", agentUser.getId());

        UUID unknownForeignerId = UUID.randomUUID();
        ForeignerDetailRequest command = new ForeignerDetailRequest(agentUser.getEmail(), unknownForeignerId);

        // when & then
        assertThatThrownBy(() ->  foreignerProfileDetailService.findForeignerDetail(command))
                .isInstanceOf(ForeignerException.class);
    }

    @Test
    @DisplayName("외국인 상세 조회는 행정사와 외국인 사이에 채팅방이 있으면 채팅방 정보를 반환한다")
    void findForeignerDetail_shouldReturnChatRoomInfo() {
        // given
        User agentUser = userTestFixture.createUser("agent@user", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "주소", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@user", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ForeignerDetailRequest command = new ForeignerDetailRequest(agentUser.getEmail(), foreignerProfile.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        ForeignerDetailResponse response = foreignerProfileDetailService.findForeignerDetail(command);

        // then

        // basicInfo 검증
        assertThat(response).isNotNull();
        assertThat(response.basicInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.basicInfo().hasChatRoomBetween()).isTrue();
        assertThat(response.basicInfo().chatRoomId()).isEqualTo(chatRoom.getId());
    }
}
