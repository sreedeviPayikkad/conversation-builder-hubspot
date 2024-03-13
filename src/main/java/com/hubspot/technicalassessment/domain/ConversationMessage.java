package com.hubspot.technicalassessment.domain;

import lombok.Builder;

@Builder
public record ConversationMessage(String content, long timestamp, int userId) {
}
