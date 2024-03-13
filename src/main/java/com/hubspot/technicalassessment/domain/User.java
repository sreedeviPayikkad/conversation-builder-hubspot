package com.hubspot.technicalassessment.domain;

import lombok.Builder;

@Builder
public record User(String avatar, String firstName, String lastName, int id) {
}
