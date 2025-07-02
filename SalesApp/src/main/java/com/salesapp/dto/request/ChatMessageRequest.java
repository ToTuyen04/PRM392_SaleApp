package com.salesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
    private Integer userID;      // Người gửi (user hoặc AI = null)
    private Integer receiverID;  // Người nhận (user, admin hoặc null nếu AI)
    private String message;      // Nội dung tin nhắn
}
