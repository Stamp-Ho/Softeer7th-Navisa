package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
class AgentProfileQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentProfileQueryService agentProfileQueryService;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @MockitoBean
    private AgentSpecializedJobService agentSpecializedJobService;

    @MockitoBean
    private AgentBadgeService agentBadgeService;

    @MockitoBean
    private UserQueryService userQueryService;

    @MockitoBean
    private AgentProfileRepository agentProfileRepository;

    @Test
    @DisplayName("FILLED_FOREIGNER 유저는 전문분야 정보를 볼 수 있다")
    void findAgentProfileCardsBasedOnFilter_filledUser_shouldSeeSpeciality() {
        // given
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

        AgentProfile profile1 = new AgentProfile("Agent A", java.time.LocalDate.now(), "url", "09:00", "Office",
                "Address", "Detail", "Hist", UUID.randomUUID(), "Lic", java.time.LocalDate.now(), "Page", "Mgmt",
                "Comment");
        ReflectionTestUtils.setField(profile1, "id", UUID.randomUUID());

        given(agentSpecializedJobService.getTop2SpecializedJobIds(any(UUID.class)))
                .willReturn(List.of(jobCode.getId()));
        given(agentBadgeService.getTop2BadgeIds(any(UUID.class))).willReturn(List.of());
        given(agentProfileRepository.findByFilters(any(), any())).willReturn(List.of(profile1));

        String email = "filled@test.com";
        User user = Mockito.mock(User.class);
        given(user.getUserType()).willReturn(UserType.FILLED_FOREIGNER);
        given(userQueryService.findByEmail(email)).willReturn(user);

        AgentCardRequest request = new AgentCardRequest(
                List.of(jobCode.getId()),
                List.of("서울"),
                List.of(language.getId()));
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileQueryService
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        List<AgentCardResponse> content = response.content();
        assertThat(content).hasSize(1);
        AgentCardResponse card = content.get(0);
        assertThat(card.agentId()).isEqualTo(profile1.getId());

        // 전문분야 정보가 포함되어야 함
        assertThat(card.agentSpecialityTop2()).isNotNull();
        assertThat(card.specialityJobCount()).isNotNull();
    }

    @Test
    @DisplayName("UNFILLED_FOREIGNER 유저는 전문분야 정보를 볼 수 없다 (Masking)")
    void findAgentProfileCardsBasedOnFilter_unfilledUser_shouldNotSeeSpeciality() {
        // given
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

        AgentProfile profile1 = new AgentProfile("Agent A", java.time.LocalDate.now(), "url", "09:00", "Office",
                "Address", "Detail", "Hist", UUID.randomUUID(), "Lic", java.time.LocalDate.now(), "Page", "Mgmt",
                "Comment");
        ReflectionTestUtils.setField(profile1, "id", UUID.randomUUID());

        given(agentSpecializedJobService.getTop2SpecializedJobIds(any(UUID.class)))
                .willReturn(List.of(jobCode.getId()));
        given(agentBadgeService.getTop2BadgeIds(any(UUID.class))).willReturn(List.of());
        given(agentProfileRepository.findByFilters(any(), any())).willReturn(List.of(profile1));

        // User mock (UNFILLED)
        String email = "unfilled@test.com";
        User user = Mockito.mock(User.class);
        given(user.getUserType()).willReturn(UserType.UNFILLED_FOREIGNER);
        given(userQueryService.findByEmail(email)).willReturn(user);

        AgentCardRequest request = new AgentCardRequest(
                List.of(jobCode.getId()),
                List.of("서울"),
                List.of(language.getId()));
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileQueryService
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        AgentCardResponse card = response.content().get(0);

        // 전문분야 정보가 Masking(null) 처리되어야 함
        assertThat(card.agentSpecialityTop2()).isNull();
        assertThat(card.specialityJobCount()).isNull();
    }

    @Test
    @DisplayName("마지막 페이지 조회 시 existNext가 false여야 한다")
    void findAgentProfileCardsBasedOnFilter_lastPage_integration() {
        // given
        String uniqueJobCode = "JC-" + UUID.randomUUID().toString().substring(0, 8);
        JobCode jobCode = agentProfileTestFixture.createJobCode(uniqueJobCode, "Job Name " + uniqueJobCode);

        String uniqueLang = "Lang-" + UUID.randomUUID().toString().substring(0, 8);
        Language language = agentProfileTestFixture.createLanguage(uniqueLang);

                
                
        AgentProfile profile1 = new AgentProfile("Agent A", java.time.LocalDate.now(), "url", "09:00", "Office", "Address", "Detail", "Hist", UUID.randomUUID(), "Lic", java.time.LocalDate.now(), "Page", "Mgmt", "Comment");
        ReflectionTestUtils.setField(profile1, "id", UUID.randomUUID());

        given(agentSpecializedJobService.getTop2SpecializedJobIds(any(UUID.class))).willReturn(List.of());
        given(agentBadgeService.getTop2BadgeIds(any(UUID.class))).willReturn(List.of());
        given(agentProfileRepository.findByFilters(any(), any())).willReturn(List.of(profile1));

        String email = "test@test.com";
        User user = Mockito.mock(User.class);
        given(user.getUserType()).willReturn(UserType.FILLED_FOREIGNER);
        given(userQueryService.findByEmail(email)).willReturn(user);

        AgentCardRequest request = new AgentCardRequest(
                List.of(jobCode.getId()),
                List.of("서울"),
                List.of(language.getId()));

        // 요청 개수(10) > 데이터 개수(1)
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);

        // when
        SliceResponse<AgentCardResponse, UUID> response = agentProfileQueryService
                .findAgentProfileCardsBasedOnFilter(request, sliceRequest, email);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.existsNext()).isFalse();
    }

}
