package com.salesapp.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String username;
    String email;
    String phoneNumber;
    String address;
    String role;

    String cartID;

    String chatMessageID;

    String notificationID;

    String orderID;
}
