package com.hubspot.technicalassessment.service;


import com.hubspot.technicalassessment.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConversationBuilderServiceImpl implements ConversationBuilderService {
    @Override
    public Inbox buildConversations(Exchanges exchanges) {

        int selfUserId = exchanges.getUserId();

        // build a user map for efficient lookup
        Map<Integer, User> userMap = buildUserMap(exchanges);

        Map<Integer, Conversation> conversationMap = new HashMap<>();
        exchanges.getMessages()
                .forEach(message -> {
                    int otherUserId = message.fromUserId() == selfUserId ? message.toUserId() : message.fromUserId();
                    /*
                     * If the conversation already exists, then update the total number of messages
                     * Also, if the current conversation is chronologically latest, then update the most recent message
                     * */
                    conversationMap.computeIfPresent(otherUserId, (unused, existingConversation) -> {
                        existingConversation.setTotalMessages(existingConversation.getTotalMessages() + 1);
                        if (message.timestamp() > existingConversation.getMostRecentMessage().timestamp()) {
                            existingConversation.setMostRecentMessage(new ConversationMessage(message.content(), message.timestamp(), message.fromUserId()));
                        }
                        return existingConversation;
                    });
                    // New message from the other userId, hence create a new conversation object
                    conversationMap.computeIfAbsent(otherUserId, unused -> this.createNewConversation(otherUserId, message, userMap));

                });

        return sortedConversations(conversationMap);

    }

    private Conversation createNewConversation(int otherUserId, Message message, Map<Integer, User> userMap) {
        return Conversation.builder()
                .userId(otherUserId)
                .firstName(userMap.get(otherUserId).firstName())
                .lastName(userMap.get(otherUserId).lastName())
                .avatar(userMap.get(otherUserId).avatar())
                .mostRecentMessage(new ConversationMessage(message.content(), message.timestamp(), message.fromUserId()))
                .totalMessages(1)
                .build();
    }

    private Inbox sortedConversations(Map<Integer, Conversation> conversationMap) {
        List<Conversation> conversations = new ArrayList<>(conversationMap.values());
        conversations.sort((conv1, conv2) -> Long.compare(conv2.getMostRecentMessage().timestamp(), conv1.getMostRecentMessage().timestamp()));
        return new Inbox(conversations);
    }

    private Map<Integer, User> buildUserMap(Exchanges exchanges) {

        return exchanges.getUsers()
                .stream()
                .collect(Collectors.toMap(User::id, user -> user));


    }
}
