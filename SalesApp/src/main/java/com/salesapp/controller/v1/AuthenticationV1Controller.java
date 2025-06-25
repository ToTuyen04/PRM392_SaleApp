package com.salesapp.controller.v1;

import com.nimbusds.jose.JOSEException;
import com.salesapp.dto.request.AuthenticationRequest;
import com.salesapp.dto.request.IntrospectRequest;
import com.salesapp.dto.request.LogoutRequest;
import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.response.AuthenticationResponse;
import com.salesapp.dto.response.IntrospectResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.service.AuthenticationService;
import com.salesapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Tag(name = "Authentication", description = "Manage Authen")
@RestController
@RequestMapping("v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationV1Controller {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseObject<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        var response = authenticationService.authenticate(request);
        return ResponseObject.<AuthenticationResponse>builder()
                .status(1000)
                .data(response)
                .message("Login successfully")
                .build();
    }

    @PostMapping("/register")
    public ResponseObject<UserResponse> register(@RequestBody UserCreateRequest request){
        var newUser = userService.createUser(request, true);
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(newUser)
                .message("Register successfully")
                .build();
    }

    @PostMapping("/logout")
    public ResponseObject<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseObject.<Void>builder()
                .status(1000)
                .message("Logout successfully")
                .build();
    }

    @PostMapping("/introspect")
    public ResponseObject<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var response = authenticationService.introspect(request);
        return ResponseObject.<IntrospectResponse>builder()
                .status(1000)
                .data(response)
                .message("Token introspection completed")
                .build();
    }
}
