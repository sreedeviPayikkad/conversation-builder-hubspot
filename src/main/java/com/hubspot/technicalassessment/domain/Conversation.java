package com.hubspot.technicalassessment.domain;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private ConversationMessage mostRecentMessage;
    private int totalMessages;
    private int userId;
    private String avatar;
    private String firstName;
    private String lastName;
}
