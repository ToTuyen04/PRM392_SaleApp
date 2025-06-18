package com.salesapp.mapper;

import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.request.UserUpdateRequest;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userID")
    UserResponse toDto(User user);
    List<UserResponse> toDto(List<User> users);
    User toUser(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User u, UserUpdateRequest request);

}
