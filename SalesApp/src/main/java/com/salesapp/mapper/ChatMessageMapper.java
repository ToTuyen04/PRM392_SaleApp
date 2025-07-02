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
    ChatMessageResponse toDto(ChatMessage message);

    List<ChatMessageResponse> toDtoList(List<ChatMessage> messages);
}
