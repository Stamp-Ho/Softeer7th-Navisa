package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.AgentReview;

import java.util.UUID;

public record FeedbackResponse(

        Long feedbackId,
        String feedbackContent,
        UUID writerId,
        String agentName,
        String agentProfileImgUrl,
        String foreignerName
) {
    public static FeedbackResponse of(AgentReview feedback, String agentName, String profileImageUrl, String foreignerName) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getFeedbackContent(),
                feedback.getAgentProfileId(),
                agentName,
                profileImageUrl,
                foreignerName
        );
    }
}
