package com.salesapp.controller.v1;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatMessageV1Controller {

    private final ChatMessageService chatMessageService;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatMessageService.sendMessage(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(@RequestParam Integer userID,
                                                                @RequestParam Integer receiverID) {
        return ResponseEntity.ok(chatMessageService.getChatHistory(userID, receiverID));
    }
}
