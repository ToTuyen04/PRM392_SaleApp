package com.salesapp.controller.v1;

import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.request.UserUpdateRequest;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "Manage Users")
@RestController
@RequestMapping("/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserV1Controller {
    @Autowired
    UserService userService;

    @DeleteMapping("/{id}")
    public ResponseObject<UserResponse> deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .message("User deleted")
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject<UserResponse> updateUser(@PathVariable int id, @RequestBody UserUpdateRequest request){
        var user = userService.updateUser(id, request);
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(user)
                .message("User updated")
                .build();
    }

    @PostMapping
    public ResponseObject<UserResponse> createUser(@RequestBody UserCreateRequest request){
        var user = userService.createUser(request, false);
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(user)
                .message("User created")
                .build();

    }

    @GetMapping("")
    public ResponseObject<List<UserResponse>> getList(){
        var users = userService.getUserList();
        return ResponseObject.<List<UserResponse>>builder()
                .status(1000)
                .data(users)
                .message("Get user list successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<UserResponse> getUser(@PathVariable int id){
        var user = userService.getUserById(id);
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(user)
                .message("Get user successfully")
                .build();
    }



}
