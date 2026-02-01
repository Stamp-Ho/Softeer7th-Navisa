package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.AgentReview;

import java.util.UUID;

public record FeedbackResponse(

        Long feedbackId,
        String feedbackContent,
        UUID writerId,
        String writerName,
        String writerProfileImgUrl
) {
    public static FeedbackResponse of(AgentReview feedback, String agentName, String profileImageUrl) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getFeedbackContent(),
                feedback.getAgentProfileId(),
                agentName,
                profileImageUrl
        );
    }
}
