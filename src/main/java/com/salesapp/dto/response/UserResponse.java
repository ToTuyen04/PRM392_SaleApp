package com.salesapp.dto.response;

import com.salesapp.entity.Cart;
import com.salesapp.entity.ChatMessage;
import com.salesapp.entity.Notification;
import com.salesapp.entity.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int userID;
    String username;
    String email;
    String phoneNumber;
    String address;
    String role;

    Cart cart;

    ChatMessage chatMessage;

    Notification notification;

    Order order;
}
