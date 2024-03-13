package com.hubspot.technicalassessment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.technicalassessment.domain.Exchanges;
import com.hubspot.technicalassessment.domain.Inbox;
import com.hubspot.technicalassessment.service.ConversationBuilderService;
import com.hubspot.technicalassessment.service.ConversationBuilderServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversationBuilderServiceImplTest {

    @Test
    @SneakyThrows
    void testSingleConversations() {
        ConversationBuilderService conversationBuilderService = new ConversationBuilderServiceImpl();
        Exchanges exchanges = getExchangesWithOneUser();
        Inbox inbox = conversationBuilderService.buildConversations(exchanges);
        assertEquals(1, inbox.getConversations().size());
        assertEquals("John", inbox.getConversations().get(0).getFirstName());
    }

    @Test
    @SneakyThrows
    void testMultipleConversation() {
        ConversationBuilderService conversationBuilderService = new ConversationBuilderServiceImpl();
        Exchanges exchanges = getExchangesWithMultipleUsers();
        Inbox inbox = conversationBuilderService.buildConversations(exchanges);
        assertEquals(2, inbox.getConversations().size());
        assertEquals("Michael", inbox.getConversations().get(0).getFirstName());
    }

    @Test
    void testEmptyConversation() {
        ConversationBuilderService conversationBuilderService = new ConversationBuilderServiceImpl();
        Exchanges exchanges = Exchanges.builder().build();
        Inbox inbox = conversationBuilderService.buildConversations(exchanges);
        assertEquals(0, inbox.getConversations().size());

    }

    private static Exchanges getExchangesWithMultipleUsers() throws JsonProcessingException {
        String requestBody = """
                    {
                      "messages": [
                          {
                              "content": "The quick brown fox jumps over the lazy dog",
                              "fromUserId": 50210,
                              "timestamp": 1529338342000,
                              "toUserId": 67452
                          },
                          {
                              "content": "Pangrams originate in the discotheque",
                              "fromUserId": 67452,
                              "timestamp": 1529075415000,
                              "toUserId": 50210
                          },
                          {
                              "content": "Have you planned your holidays this year yet?",
                              "fromUserId": 67452,
                              "timestamp": 1529542953000,
                              "toUserId": 50210
                         },
                         {
                              "content": "Strange noises have been heard on the moors",
                              "fromUserId": 78596,
                              "timestamp": 1533112961000,
                              "toUserId": 50210
                         },
                         {
                             "content": "You go straight ahead for two hundred yards and then take the first right turn",
                             "fromUserId": 50210,
                             "timestamp": 1533197225000,
                             "toUserId": 78596
                         },
                         {
                             "content": "It's a privilege and an honour to have known you",
                             "fromUserId": 78596,
                             "timestamp": 1533118270000,
                             "toUserId": 50210
                         }
                      ],
                      "userId": 50210,
                      "users": [
                          {
                              "avatar": "octocat.jpg",
                              "firstName": "John",
                              "lastName": "Doe",
                              "id": 67452
                          },
                          {
                              "avatar": "genie.png",
                              "firstName": "Michael",
                              "lastName": "Crowley",
                              "id": 78596
                          }
                      ]
                  }
                """;
        return new ObjectMapper().readValue(requestBody, Exchanges.class);
    }

    private static Exchanges getExchangesWithOneUser() throws JsonProcessingException {
        String requestBody = """
                {
                    "messages": [
                        {
                            "content": "The quick brown fox jumps over the lazy dog",
                            "fromUserId": 50210,
                            "timestamp": 1529338342000,
                            "toUserId": 67452
                        },
                        {
                            "content": "Pangrams originate in the discotheque",
                            "fromUserId": 67452,
                            "timestamp": 1529075415000,
                            "toUserId": 50210
                        },
                        {
                            "content": "Have you planned your holidays this year yet?",
                            "fromUserId": 67452,
                            "timestamp": 1529542953000,
                            "toUserId": 50210
                       }
                    ],
                    "userId": 50210,
                    "users": [
                        {
                            "avatar": "octocat.jpg",
                            "firstName": "John",
                            "lastName": "Doe",
                            "id": 67452
                        },
                        {
                            "avatar": "genie.png",
                            "firstName": "Michael",
                            "lastName": "Crowley",
                            "id": 78596
                        }
                    ]
                }
                """;
        return new ObjectMapper().readValue(requestBody, Exchanges.class);
    }
}
