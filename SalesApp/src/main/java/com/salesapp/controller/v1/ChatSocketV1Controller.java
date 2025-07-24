package com.salesapp.controller.v1;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatSocketV1Controller {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request) {
        // Gọi service xử lý (lưu DB + gửi về)
        ChatMessageResponse response = chatMessageService.sendMessage(request);


        if (request.getReceiverID() != null && request.getReceiverID() == -1) {
            return; // đã gửi AI reply từ service rồi
        }


        messagingTemplate.convertAndSendToUser(
                String.valueOf(response.getReceiverID()),
                "/queue/messages",
                response
        );
    }
}
