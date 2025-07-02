package com.salesapp.service;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.entity.ChatMessage;
import com.salesapp.entity.User;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.ChatMessageMapper;
import com.salesapp.repository.ChatMessageRepository;
import com.salesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AIService aiService;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        User sender = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        User receiver = null;
        if (request.getReceiverID() != null) {
            receiver = userRepository.findById(request.getReceiverID())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        }

        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserID(sender);
        userMsg.setReceiver(receiver);
        userMsg.setMessage(request.getMessage());
        userMsg.setSentAt(Instant.now());
        userMsg.setFromAI(false);
        chatMessageRepository.save(userMsg);

        if (containsRequestForHuman(request.getMessage())) {
            ChatMessage forwardNotice = new ChatMessage();
            forwardNotice.setUserID(null); // Hệ thống/Ai
            forwardNotice.setReceiver(sender);
            forwardNotice.setMessage("Tôi sẽ kết nối bạn với nhân viên hỗ trợ ngay bây giờ...");
            forwardNotice.setSentAt(Instant.now());
            forwardNotice.setFromAI(true);
            forwardNotice.setForwardedToHuman(true);
            chatMessageRepository.save(forwardNotice);

            return chatMessageMapper.toDto(forwardNotice);
        }

        String aiReply = aiService.generateReply(request.getMessage());

        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserID(null); // AI là hệ thống
        aiMsg.setReceiver(sender);
        aiMsg.setMessage(aiReply);
        aiMsg.setSentAt(Instant.now());
        aiMsg.setFromAI(true);
        chatMessageRepository.save(aiMsg);

        return chatMessageMapper.toDto(aiMsg);
    }

    public List<ChatMessageResponse> getChatBetween(Integer userID, Integer receiverID) {
        User user1 = userRepository.findById(userID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        User user2 = userRepository.findById(receiverID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        List<ChatMessage> messages = chatMessageRepository.findChatBetweenUsers(user1, user2);
        return chatMessageMapper.toDtoList(messages);
    }

    public List<ChatMessageResponse> getUserChatHistory(Integer userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        List<ChatMessage> messages = chatMessageRepository.findChatBetweenUsers(user, user);
        return chatMessageMapper.toDtoList(messages);
    }

    public void saveMessage(Integer userID, String message) {
        userRepository.findById(userID).ifPresent(user -> {
            ChatMessage chat = new ChatMessage();
            chat.setUserID(user);
            chat.setMessage(message);
            chat.setSentAt(Instant.now());
            chat.setFromAI(false);
            chatMessageRepository.save(chat);
        });
    }

    public String replyFromAI(Integer receiverID, String message) {
        String reply = aiService.generateReply(message);
        Optional<User> receiver = userRepository.findById(receiverID);
        if (receiver.isPresent()) {
            ChatMessage msg = new ChatMessage();
            msg.setUserID(null);
            msg.setReceiver(receiver.get());
            msg.setMessage(reply);
            msg.setSentAt(Instant.now());
            msg.setFromAI(true);
            chatMessageRepository.save(msg);
        }
        return reply;
    }

    public List<String> getSuggestions() {
        return List.of(
                "Tôi muốn biết sản phẩm mới nhất?",
                "Tôi muốn hỗ trợ đặt hàng",
                "Tôi có câu hỏi về thanh toán",
                "Tôi muốn gặp nhân viên"
        );
    }

    private boolean containsRequestForHuman(String message) {
        String lower = message.toLowerCase();
        return lower.contains("gặp nhân viên") ||
                lower.contains("nói chuyện với người") ||
                lower.contains("gặp hỗ trợ") ||
                lower.contains("gặp admin") ||
                lower.contains("nói chuyện trực tiếp");
    }
}
