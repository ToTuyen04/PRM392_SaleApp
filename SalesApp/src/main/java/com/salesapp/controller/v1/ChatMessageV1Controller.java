package com.salesapp.controller.v1;

import com.salesapp.dto.request.ChatMessageRequest;
import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.entity.ChatMessage;
import com.salesapp.mapper.ChatMessageMapper;
import com.salesapp.repository.ChatMessageRepository;
import com.salesapp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatMessageV1Controller {

    private final ChatMessageService chatMessageService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatMessageService.sendMessage(request));
    }

//    @GetMapping("/history")
//    public ResponseEntity<List<ChatMessageResponse>> getHistory(@RequestParam Integer userID,
//                                                                @RequestParam(required = false) Integer receiverID) {
//        return ResponseEntity.ok(chatMessageService.getChatHistory(userID, receiverID));
//    }
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSeparatedHistory(@RequestParam Integer userID) {
        return ResponseEntity.ok(chatMessageService.getSeparatedChatHistory(userID));
    }
    @GetMapping("/forwarded")
    public ResponseEntity<List<ChatMessageResponse>> getForwardedMessages() {
        List<ChatMessage> forwarded = chatMessageRepository.findByForwardedToHumanTrueOrderBySentAtDesc();
        return ResponseEntity.ok(forwarded.stream()
                .map(chatMessageMapper::toResponse)
                .toList());
    }


}
