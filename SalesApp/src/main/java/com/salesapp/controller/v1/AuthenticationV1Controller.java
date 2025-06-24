package com.salesapp.controller.v1;

import com.salesapp.dto.request.AuthenticationRequest;
import com.salesapp.dto.response.AuthenticationResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Manage Authen")
@RestController
@RequestMapping("v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationV1Controller {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseObject<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        var response = authenticationService.authenticate(request);
        return ResponseObject.<AuthenticationResponse>builder()
                .status(1000)
                .data(response)
                .message("Login successfully")
                .build();
    }
}
