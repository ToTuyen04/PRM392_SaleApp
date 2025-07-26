package com.salesapp.controller.v1;

import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.dto.response.CustomerUserResponse;
import com.salesapp.entity.ChatMessage;
import com.salesapp.mapper.ChatMessageMapper;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/admin/chat")
@RequiredArgsConstructor
public class AdminChatV1Controller {

    private final ChatMessageService chatMessageService;
    private final ChatMessageMapper chatMessageMapper;


    @GetMapping("/history")
    public ResponseEntity<Map<Integer, List<ChatMessageResponse>>> getAllUserChatHistories() {
        List<ChatMessage> allMessages = chatMessageService.getAllMessagesBetweenUsersAndAdminOrAI();

        Map<Integer, List<ChatMessageResponse>> chatByUser = allMessages.stream()
                .filter(msg -> !msg.getUserID().getRole().isAdminOrAI() || !msg.getReceiver().getRole().isAdminOrAI()) // ít nhất 1 trong 2 là customer
                .flatMap(msg -> {
                    // Xác định đâu là customer
                    Integer customerID = msg.getUserID().getRole().isCustomer() ? msg.getUserID().getId() : msg.getReceiver().getId();
                    return Stream.of(new AbstractMap.SimpleEntry<>(customerID, chatMessageMapper.toResponse(msg)));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return ResponseEntity.ok(chatByUser);
    }

    @GetMapping("/ListCustomer")
    public ResponseEntity<List<CustomerUserResponse>> getCustomerUsersWhoChatted() {
        return ResponseEntity.ok(chatMessageService.getAllCustomerUsersInChat());
    }

}
