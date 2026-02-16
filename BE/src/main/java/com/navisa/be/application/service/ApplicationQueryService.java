package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.dto.projection.VisaApplicationFormProjection;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationCardResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationQueryService {

    private final ApplicationFormRepository visaApplicationFormRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final UserQueryService userQueryService;
    private final AgentProfileCrudService agentProfileQueryService;
    private final StorageService storageService;

    // 행정사의 최근 수정 문서 조회
    public List<RecentVisaFormsResponse> getRecentVisaForms(String email) {
        User user = userQueryService.findByEmail(email);

        if (!user.getUserType().equals(UserType.VALID_AGENT)) {
            throw new ApplicationException(ResponseStatus.AGENT_NOT_APPROVED);
        }

        AgentProfile agent = agentProfileQueryService.findByUserId(user.getId());

        List<VisaApplicationForm> forms = visaApplicationFormRepository
                .findTop6ByAgentProfileOrderByUpdatedAtDesc(agent);

        return forms.stream()
                .map(form -> new RecentVisaFormsResponse(
                        form.getId(),
                        form.getForeignerProfile().getNickname(),
                        form.isDone(),
                        form.getCurrentStep(),
                        form.getProfileObjectKey(),
                        form.getUpdatedAt()
                ))
                .toList();
    }

    // 행정사의 비자 신청서 조회(필터 가능)
    public SliceResponse<VisaApplicationCardResponse, UUID> findVisaFormsByFilter(
            String email, SliceRequest<UUID> slice, Boolean complete) {

        User findUser = userQueryService.findByEmail(email);

        AgentProfile agentProfile = agentProfileQueryService.findByUserId(findUser.getId());

        List<VisaApplicationFormProjection> visaApplicationFormProjections =
                visaApplicationFormRepository.findAllByNoOffsetAndFilter(agentProfile.getId(), slice, complete);

        boolean existsNext = visaApplicationFormProjections.size() > slice.size();

        List<VisaApplicationFormProjection> contentVisaApplicationFormProjections = existsNext
                ? visaApplicationFormProjections.subList(0, slice.size())
                : visaApplicationFormProjections;

        UUID lastElementId = contentVisaApplicationFormProjections.isEmpty()
                ? null
                : contentVisaApplicationFormProjections.get(contentVisaApplicationFormProjections.size() - 1).id();

        return new SliceResponse<>(
                contentVisaApplicationFormProjections.stream().map(
                        projection -> VisaApplicationCardResponse.projectionToDto(
                                projection,
                                storageService.getImgUrl(ImageSize.MEDIUM, projection.profileObjectKey(), true)
                        )).toList(),
                existsNext,
                lastElementId);
    }

    // 외국인용 최신 비자신청서
    public VisaApplicationDetailResponse getLatestVisaFormForForeigner(String email) {
        User user = userQueryService.findByEmail(email);

        ForeignerProfile foreigner = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApplicationException(ResponseStatus.INVALID_FOREIGNER));

        VisaApplicationForm form = visaApplicationFormRepository
                .findFirstByForeignerProfileOrderByCreatedAtDesc(foreigner)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        List<Map<String, Object>> sections = mergeSections(form);

        String profileImgUrl = (form.getProfileObjectKey() != null)
                ? storageService.getImgUrl(ImageSize.MEDIUM, form.getProfileObjectKey(), true)
                : null;

        return new VisaApplicationDetailResponse(
                form.getId(),
                profileImgUrl,
                form.isDone(),
                form.getUpdatedAt(),
                form.getTotalCount(),
                form.getCurrentStep(),
                sections
        );
    }

    // 행정사용 특정 비자 신청서 조회
    public VisaApplicationDetailResponse getVisaFormForAgent(String email, UUID applicationFormId) {
        User user = userQueryService.findByEmail(email);

        AgentProfile agent = agentProfileQueryService.findByUserId(user.getId());

        VisaApplicationForm form = visaApplicationFormRepository.findWithAgentProfileById(applicationFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        // 이 신청서의 담당 행정사가 현재 로그인한 행정사인지 확인
        if (form.getAgentProfile() == null || !form.getAgentProfile().getId().equals(agent.getId())) {
            throw new ApplicationException(ResponseStatus.FORBIDDEN);
        }

        String profileImgUrl = (form.getProfileObjectKey() != null)
                ? storageService.getImgUrl(ImageSize.MEDIUM, form.getProfileObjectKey(), true)
                : null;

        return new VisaApplicationDetailResponse(
                form.getId(),
                profileImgUrl,
                form.isDone(),
                form.getUpdatedAt(),
                form.getTotalCount(),
                form.getCurrentStep(),
                mergeSections(form)
        );
    }

    private List<Map<String, Object>> mergeSections(VisaApplicationForm form) {
        List<Map<String, Object>> sections = new ArrayList<>();

        addSectionWithId(sections, form.getPersonalDetail(), 1);
        addSectionWithId(sections, form.getPassportInformation(), 2);
        addSectionWithId(sections, form.getContactInformation(), 3);
        addSectionWithId(sections, form.getMaritalStatusAndFamilyDetails(), 4);
        addSectionWithId(sections, form.getEducation(), 5);
        addSectionWithId(sections, form.getEmployment(), 6);
        addSectionWithId(sections, form.getVisitInformation(), 7);
        addSectionWithId(sections, form.getHelpInformation(), 8);
        addSectionWithId(sections, form.getInviteInformation(), 9);

        return sections;
    }

    private void addSectionWithId(List<Map<String, Object>> list, Map<String, Object> data, int id) {
        Map<String, Object> sectionMap;

        if (data == null) {
            sectionMap = new HashMap<>();
        } else {
            sectionMap = new HashMap<>(data);
        }

        sectionMap.put("sectionId", id);
        list.add(sectionMap);
    }

    public VisaApplicationForm findCurrentApplicationForm(UUID foreignerId, UUID agentId) {
        return visaApplicationFormRepository.findCurrentAppFormNative(foreignerId, agentId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));
    }
}
