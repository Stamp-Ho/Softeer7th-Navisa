package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.dto.projection.ApplicationFormProjection;
import com.navisa.be.application.dto.response.ApplicationFormCardResponse;
import com.navisa.be.application.dto.response.ApplicationFormDetailResponse;
import com.navisa.be.application.dto.response.RecentApplicationFormsResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.response.SliceResponse;
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
public class ApplicationFormSearchService {

    private final ApplicationFormRepository applicationFormRepository;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final UserQueryService userQueryService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final StorageService storageService;

    public List<RecentApplicationFormsResponse> getRecentApplicationForms(String email) {
        User user = userQueryService.findByEmail(email);

        if (!user.getUserType().equals(UserType.VALID_AGENT)) {
            throw new ApplicationFormException(ResponseStatus.AGENT_NOT_APPROVED);
        }

        AgentProfile agent = agentProfileCrudService.findByUserId(user.getId());

        List<ApplicationForm> forms = applicationFormRepository
                .findTop6ByAgentProfileOrderByUpdatedAtDesc(agent);

        return forms.stream()
                .map(form -> new RecentApplicationFormsResponse(
                        form.getId(),
                        form.getForeignerProfile().getNickname(),
                        form.isDone(),
                        form.getCurrentStep(),
                        storageService.getImgUrl(ImageSize.MEDIUM, form.getProfileObjectKey(), true),
                        form.getUpdatedAt()))
                .toList();
    }

    public SliceResponse<ApplicationFormCardResponse, UUID> findApplicationFormsByFilter(
            String email, SliceRequest<UUID> slice, Boolean complete) {

        User findUser = userQueryService.findByEmail(email);
        AgentProfile agentProfile = agentProfileCrudService.findByUserId(findUser.getId());

        List<ApplicationFormProjection> applicationFormProjections = applicationFormRepository
                .findAllByNoOffsetAndFilter(agentProfile.getId(), slice, complete);

        boolean existsNext = applicationFormProjections.size() > slice.size();

        List<ApplicationFormProjection> contentApplicationFormProjections = existsNext
                ? applicationFormProjections.subList(0, slice.size())
                : applicationFormProjections;

        UUID lastElementId = contentApplicationFormProjections.isEmpty()
                ? null
                : contentApplicationFormProjections.get(contentApplicationFormProjections.size() - 1).id();

        return new SliceResponse<>(
                contentApplicationFormProjections.stream().map(
                        projection -> ApplicationFormCardResponse.projectionToDto(
                                projection,
                                storageService.getImgUrl(ImageSize.MEDIUM, projection.profileObjectKey(), true)))
                        .toList(),
                existsNext,
                lastElementId);
    }

    public ApplicationFormDetailResponse getLatestApplicationFormForForeigner(String email) {
        User user = userQueryService.findByEmail(email);

        ForeignerProfile foreigner = foreignerProfileCrudService.findByUserId(user.getId());

        ApplicationForm form = applicationFormRepository
                .findFirstByForeignerProfileOrderByCreatedAtDesc(foreigner)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        List<Map<String, Object>> sections = mergeSections(form);

        String profileImgUrl = (form.getProfileObjectKey() != null)
                ? storageService.getImgUrl(ImageSize.MEDIUM, form.getProfileObjectKey(), true)
                : null;

        return new ApplicationFormDetailResponse(
                form.getId(),
                profileImgUrl,
                form.isDone(),
                form.getUpdatedAt(),
                form.getTotalCount(),
                form.getCurrentStep(),
                sections);
    }

    public ApplicationFormDetailResponse getApplicationFormForAgent(String email, UUID applicationFormId) {
        User user = userQueryService.findByEmail(email);
        AgentProfile agent = agentProfileCrudService.findByUserId(user.getId());

        ApplicationForm form = applicationFormRepository.findWithAgentProfileById(applicationFormId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        // 이 신청서의 담당 행정사가 현재 로그인한 행정사인지 확인
        if (form.getAgentProfile() == null || !form.getAgentProfile().getId().equals(agent.getId())) {
            throw new ApplicationFormException(ResponseStatus.FORBIDDEN_ACCESS);
        }

        String profileImgUrl = (form.getProfileObjectKey() != null)
                ? storageService.getImgUrl(ImageSize.MEDIUM, form.getProfileObjectKey(), true)
                : null;

        return new ApplicationFormDetailResponse(
                form.getId(),
                profileImgUrl,
                form.isDone(),
                form.getUpdatedAt(),
                form.getTotalCount(),
                form.getCurrentStep(),
                mergeSections(form));
    }

    private List<Map<String, Object>> mergeSections(ApplicationForm form) {
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
}
