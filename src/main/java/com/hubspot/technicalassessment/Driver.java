package com.hubspot.technicalassessment;

import com.hubspot.technicalassessment.client.RestServiceClient;
import com.hubspot.technicalassessment.config.AppConfig;
import com.hubspot.technicalassessment.domain.Exchanges;
import com.hubspot.technicalassessment.domain.Inbox;
import com.hubspot.technicalassessment.service.ConversationBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class Driver implements CommandLineRunner {
    private final RestServiceClient restServiceClient;
    private final ConversationBuilderService conversationBuilderService;

    @Override
    public void run(String... args) throws Exception {

        /*
         * Driver program to fetch all the message exchanges, use the build conversation service to populate the inbox
         * and post it back to the result api
         * */
        Exchanges messages = restServiceClient.getMessages(AppConfig.GET_API);
        Inbox inbox = conversationBuilderService.buildConversations(Objects.requireNonNull(messages));
        restServiceClient.postInbox(AppConfig.POST_API, inbox);

    }
}
