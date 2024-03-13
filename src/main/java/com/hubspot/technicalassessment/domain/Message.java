package com.hubspot.technicalassessment.domain;

import lombok.Builder;

@Builder
public record Message(String content, int fromUserId, long timestamp, int toUserId) {
}
