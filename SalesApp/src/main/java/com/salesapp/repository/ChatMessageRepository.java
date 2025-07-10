package com.salesapp.repository;

import com.salesapp.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // ✅ Tìm lịch sử giữa 2 người dùng bất kỳ (bao gồm cả AI)
    @Query("""
        SELECT m FROM ChatMessage m 
        WHERE 
            (m.userID.id = :userID AND m.receiver.id = :receiverID) OR 
            (m.userID.id = :receiverID AND m.receiver.id = :userID)
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findChatBetweenUsers(Integer userID, Integer receiverID);

    // ✅ Lấy lịch sử với AI (ID mặc định là 23)
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE 
            (m.userID.id = :userID AND m.receiver.id = 23) OR 
            (m.userID.id = 23 AND m.receiver.id = :userID)
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findChatWithAI(Integer userID);
}
