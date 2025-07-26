package com.salesapp.repository;

import com.salesapp.entity.ChatMessage;
import com.salesapp.entity.User;
import com.salesapp.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    @Query("""
    SELECT m FROM ChatMessage m 
    WHERE 
        (m.userID.id = :userID AND m.receiver.role = :role) OR 
        (m.receiver.id = :userID AND m.userID.role = :role)
    ORDER BY m.sentAt ASC
""")
    List<ChatMessage> findChatByUserAndRole(Integer userID, RoleEnum role);
    List<ChatMessage> findByForwardedToHumanTrueOrderBySentAtDesc();

    List<ChatMessage> findAllByOrderBySentAtAsc();

    @Query("SELECT DISTINCT m.userID FROM ChatMessage m WHERE m.userID.role = 'CUSTOMER'")
    List<User> findDistinctCustomersWhoSentMessages();


}
