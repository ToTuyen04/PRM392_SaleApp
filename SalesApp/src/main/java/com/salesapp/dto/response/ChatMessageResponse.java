package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChatMessageResponse {
    private Integer userID;           // Người gửi
    private Integer receiverID;       // Người nhận
    private String message;           // Nội dung
    private Instant sentAt;           // Thời điểm gửi
    private boolean fromAI;           // Tin nhắn có phải từ AI không
    private boolean needHumanSupport; // Có cần chuyển tới nhân viên không
}
