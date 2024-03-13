package com.hubspot.technicalassessment.domain;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inbox {
    private List<Conversation> conversations;
}
