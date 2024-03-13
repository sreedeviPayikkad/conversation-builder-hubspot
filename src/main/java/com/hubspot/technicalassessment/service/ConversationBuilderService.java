package com.hubspot.technicalassessment.service;

import com.hubspot.technicalassessment.domain.Exchanges;
import com.hubspot.technicalassessment.domain.Inbox;

public interface ConversationBuilderService {
    Inbox buildConversations(Exchanges exchanges);
}
