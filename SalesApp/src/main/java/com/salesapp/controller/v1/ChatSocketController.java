package com.salesapp.controller.v1;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.entity.ChatMessageSocket;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    // Nhận tin nhắn từ user gửi qua websocket và lưu
    @MessageMapping("/chat")
    public void handleSocketMessage(@Payload ChatMessageSocket socketMsg) {
        chatMessageService.saveMessage(socketMsg.getUserID(), socketMsg.getMessage());

        // Gửi phản hồi từ AI nếu không gửi cho admin
        if (!socketMsg.isFromAI() && !socketMsg.isToAdmin()) {
            String aiReply = chatMessageService.replyFromAI(socketMsg.getUserID(), socketMsg.getMessage());

            ChatMessageSocket aiMsg = new ChatMessageSocket(
                    socketMsg.getUserID(),
                    aiReply,
                    true,
                    false,
                    Instant.now()
            );

            // Gửi tin nhắn trả lời từ AI về client
            messagingTemplate.convertAndSend("/topic/messages/" + socketMsg.getUserID(), aiMsg);
        }
    }

    // Gửi tin nhắn từ người dùng đến người khác (ví dụ: admin)
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request) {
        ChatMessageResponse response = chatMessageService.sendMessage(request);

        if (request.getReceiverID() != null) {
            String destination = "/user/" + request.getReceiverID() + "/queue/messages";
            messagingTemplate.convertAndSend(destination, response);
        }
    }
}
