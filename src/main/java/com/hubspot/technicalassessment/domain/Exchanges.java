package com.hubspot.technicalassessment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exchanges {
    @Builder.Default
    private List<Message> messages = new ArrayList<>();
    private int userId;
    @Builder.Default
    private List<User> users = new ArrayList<>();
    ;
}
