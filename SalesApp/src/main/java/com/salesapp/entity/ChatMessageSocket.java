package com.salesapp.entity;

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
    private boolean isFromAI;
    private boolean isToAdmin;
    private Instant sentAt;
}
