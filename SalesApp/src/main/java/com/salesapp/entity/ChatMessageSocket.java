package com.salesapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSocket {

    private Integer userID;

    @Lob
    @Column(name = "Message")
    private String message;

    @JsonProperty("fromAI")
    private boolean isFromAI;

    @JsonProperty("toAdmin")
    private boolean isToAdmin;

    private Instant sentAt;
}
