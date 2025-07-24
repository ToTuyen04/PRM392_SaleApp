package com.salesapp.repository;

import com.salesapp.entity.ChatMessage;
import com.salesapp.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

//    @Query("""
//    SELECT m FROM ChatMessage m
//    WHERE
//        (m.userID.id = :userID AND m.receiver.role IN (:roles)) OR
//        (m.receiver.id = :userID AND m.userID.role IN (:roles))
//    ORDER BY m.sentAt ASC
//""")
//    List<ChatMessage> findChatWithRoles(Integer userID, List<RoleEnum> roles);
//
//    @Query("""
//    SELECT m FROM ChatMessage m
//    WHERE
//        (m.userID.id = :userID AND m.receiver.id = :receiverID) OR
//        (m.userID.id = :receiverID AND m.receiver.id = :userID)
//    ORDER BY m.sentAt ASC
//""")
//    List<ChatMessage> findChatBetweenUsers(Integer userID, Integer receiverID);

    @Query("""
    SELECT m FROM ChatMessage m 
    WHERE 
        (m.userID.id = :userID AND m.receiver.role = :role) OR 
        (m.receiver.id = :userID AND m.userID.role = :role)
    ORDER BY m.sentAt ASC
""")
    List<ChatMessage> findChatByUserAndRole(Integer userID, RoleEnum role);
    List<ChatMessage> findByForwardedToHumanTrueOrderBySentAtDesc();


}
