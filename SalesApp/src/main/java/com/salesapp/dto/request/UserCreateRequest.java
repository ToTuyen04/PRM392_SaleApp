package com.salesapp.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    String username;
    String password; // Đổi từ passwordHash thành password
    String email;
    String phoneNumber;
    String address;
    String role;

    String cartID;

    String chatMessageID;

    String notificationID;

    String orderID;
}
