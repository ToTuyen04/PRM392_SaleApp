package com.salesapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ChatMessages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChatMessageID", nullable = false)
    private Integer id;

    // Người gửi (user khách)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User userID;

    // Người nhận (AI hoặc nhân viên - cũng là User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReceiverID")
    private User receiver;

    @Lob
    @Column(name = "Message", nullable = false)
    private String message;

    @Column(name = "SentAt", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant sentAt;

    @Column(name = "FromAI", nullable = false)
    @ColumnDefault("false")
    private boolean fromAI;

    @Column(name = "ForwardedToHuman", nullable = false)
    @ColumnDefault("false")
    private boolean forwardedToHuman;
}
