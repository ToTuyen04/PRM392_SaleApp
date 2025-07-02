package com.salesapp.repository;

import com.salesapp.entity.ChatMessage;
import com.salesapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // Lấy toàn bộ tin nhắn mà user là người gửi hoặc người nhận
    List<ChatMessage> findAllByUserIDOrReceiverOrderBySentAtAsc(User userID, User receiver);

    // Lấy cuộc trò chuyện 2 chiều giữa 2 người
    @Query("SELECT c FROM ChatMessage c WHERE " +
            "(c.userID = :user1 AND c.receiver = :user2) OR " +
            "(c.userID = :user2 AND c.receiver = :user1) " +
            "ORDER BY c.sentAt ASC")
    List<ChatMessage> findChatBetweenUsers(User user1, User user2);
}
