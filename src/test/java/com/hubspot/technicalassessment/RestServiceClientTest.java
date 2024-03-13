package com.hubspot.technicalassessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.technicalassessment.client.RestServiceClient;
import com.hubspot.technicalassessment.domain.Exchanges;
import com.hubspot.technicalassessment.domain.Inbox;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestServiceClientTest {
    private static MockWebServer mockWebServer;
    private RestServiceClient restServiceClient;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);
        setupPaths();
    }

    private static void setupPaths() {
        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                String path = request.getPath().split("\\?")[0];
                return switch (path) {
                    case "/dataset" -> new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .setBody(getExchanges());
                    case "/result" -> {
                        System.out.println("Posted Request: " + request.getBody().size());
                        yield new MockResponse().setResponseCode(200).setBody("Success");
                    }

                    default -> throw new RuntimeException("unknow path");
                };
            }
        });
    }


    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setupClient() {
        WebClient webClient = WebClient
                .builder()
                .baseUrl("http://localhost:8080")
                .build();

        restServiceClient = new RestServiceClient(webClient, new ObjectMapper());
    }

    @Test
    void testGetExchanges() {
        Exchanges messages = restServiceClient.getMessages("/dataset");
        System.out.println(messages.getMessages().get(0));
        assertEquals(6, messages.getMessages().size());
        assertEquals(1529338342000L, messages.getMessages().get(0).timestamp());
        assertEquals(50210, messages.getMessages().get(0).fromUserId());
        assertEquals(67452, messages.getMessages().get(0).toUserId());
        assertEquals("The quick brown fox jumps over the lazy dog", messages.getMessages().get(0).content());
    }

    @Test
    void testPostInbox() {
        String response = restServiceClient.postInbox("/result", getInbox());
        assertEquals("Success", response);
    }

    @SneakyThrows
    private Inbox getInbox() {
        String requestBody = """
                    {
                    "conversations": [
                        {
                            "avatar": "genie.png",
                            "firstName": "Michael",
                            "lastName": "Crowley",
                            "mostRecentMessage": {
                                "content": "You go straight ahead for two hundred yards and then take the first right turn",
                                "timestamp": 1533197225000,
                                "userId": 50210
                            },
                            "totalMessages": 3,
                            "userId": 78596
                        },
                        {
                            "avatar": "octocat.jpg",
                            "firstName": "John",
                            "lastName": "Doe",
                            "mostRecentMessage": {
                                "content": "Have you planned your holidays this year yet?",
                                "timestamp": 1529542953000,
                                "userId": 67452
                            },
                            "totalMessages": 3,
                            "userId": 67452
                      }
                    ]
                }
                """;
        return new ObjectMapper().readValue(requestBody, Inbox.class);
    }

    private static String getExchanges() {
        return """
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
    }


}
