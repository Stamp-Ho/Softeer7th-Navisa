package com.navisa.be.agent.service;

import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AgentProfileCrudService {

    private final AgentProfileRepository agentProfileRepository;

    @Transactional
    public void syncAgentLoginActivity(UUID userId) {
        AgentProfile profile = agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));

        profile.syncLoginInfo();
    }

    public AgentProfile findWithSpecializedJobByUserId(UUID userId) {
        return agentProfileRepository.findWithSpecializedJobByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_USER));
    }

    public AgentProfile findByUserId(UUID userId) {
        return agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));
    }

    public boolean existsByUserId(UUID userId) {
        return agentProfileRepository.existsByUserId(userId);
    }

    public AgentProfile findById(UUID id) {
        return agentProfileRepository.findById(id)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));
    }
}
