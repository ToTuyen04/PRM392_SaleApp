package com.salesapp.mapper;

import com.salesapp.dto.response.ChatMessageResponse;
import com.salesapp.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(source = "userID.id", target = "userID")
    @Mapping(source = "receiver.id", target = "receiverID")
    ChatMessageResponse toResponse(ChatMessage entity);

    // Không cần tạo từ request → entity vì dùng service xử lý trực tiếp do có User thực thể.
}