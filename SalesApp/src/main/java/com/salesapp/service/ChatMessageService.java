package com.salesapp.service;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.entity.ChatMessage;
import com.salesapp.entity.User;
import com.salesapp.mapper.ChatMessageMapper;
import com.salesapp.repository.ChatMessageRepository;
import com.salesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final GeminiService geminiService;

    private static final Integer AI_USER_ID = 23;

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        User sender = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserID()));

        User receiver = userRepository.findById(request.getReceiverID())
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + request.getReceiverID()));

        // 🔵 Lưu tin nhắn người gửi
        ChatMessage message = new ChatMessage();
        message.setUserID(sender);
        message.setReceiver(receiver);
        message.setMessage(request.getMessage());
        message.setSentAt(Instant.now());
        message.setFromAI(false);
        message.setForwardedToHuman(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 🔁 Nếu gửi tới AI → phản hồi tự động
        if (receiver.getId().equals(AI_USER_ID)) {
            String aiReply = geminiService.getResponseFromAI(request.getMessage());

            User aiUser = receiver;

            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setUserID(aiUser);                  // người gửi là AI
            aiMessage.setReceiver(sender);                // gửi ngược về cho user
            aiMessage.setMessage(aiReply);
            aiMessage.setSentAt(Instant.now());
            aiMessage.setFromAI(true);
            aiMessage.setForwardedToHuman(false);

            ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);

            // Gửi về client qua WebSocket
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(sender.getId()),
                    "/queue/messages",
                    chatMessageMapper.toResponse(savedAiMessage)
            );
        }

        return chatMessageMapper.toResponse(savedMessage);
    }

    public List<ChatMessageResponse> getChatHistory(Integer userID, Integer receiverID) {
        List<ChatMessage> messages;

        if (receiverID.equals(AI_USER_ID)) {
            messages = chatMessageRepository.findChatWithAI(userID);
        } else {
            messages = chatMessageRepository.findChatBetweenUsers(userID, receiverID);
        }

        return messages.stream()
                .map(chatMessageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
