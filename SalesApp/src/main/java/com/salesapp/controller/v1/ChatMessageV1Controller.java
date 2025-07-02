package com.salesapp.controller.v1;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatMessageV1Controller {

    private final ChatMessageService chatService;

    @PostMapping
    public ResponseEntity<ChatMessageResponse> send(@RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/history/{userID}")
    public ResponseEntity<List<ChatMessageResponse>> history(@PathVariable Integer userID) {
        return ResponseEntity.ok(chatService.getUserChatHistory(userID));
    }

    @GetMapping("/ai/suggestions")
    public ResponseEntity<List<String>> suggestions() {
        return ResponseEntity.ok(chatService.getSuggestions());
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @RequestParam Integer userID,
            @RequestParam Integer receiverID
    ) {
        return ResponseEntity.ok(chatService.getChatBetween(userID, receiverID));
    }
}
