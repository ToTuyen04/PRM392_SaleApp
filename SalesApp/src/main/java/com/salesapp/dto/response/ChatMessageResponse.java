package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChatMessageResponse {
    private Integer userID;
    private Integer receiverID;
    private String message;
    private Instant sentAt;

    private boolean fromAI;
    private boolean forwardedToHuman;
    private boolean toAdmin;

}
