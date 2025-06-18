package com.salesapp.controller.v2;

import com.salesapp.dto.response.ResponseObject;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.User;
import com.salesapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User", description = "Manage Users")
@RestController
@RequestMapping("/v2/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerV2 {
    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseObject getList(){
        return ResponseObject.builder()
                .status(1000)
                .message("User List")
                .data(userService.getUserList())
                .build();
    }

}
