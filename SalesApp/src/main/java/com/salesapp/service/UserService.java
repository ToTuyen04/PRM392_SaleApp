package com.salesapp.service;

import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.request.UserUpdateRequest;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.User;
import com.salesapp.enums.RoleEnum;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.UserMapper;
import com.salesapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;
    //lấy ra danh sách người dùng
    public List<UserResponse> getUserList(){
        var list = userRepository.findAll();
        return userMapper.toDto(list);
    }

    public UserResponse getUserById(int id){
        return userMapper.toDto(userRepository.findById(id).get());
    }

    public UserResponse createUser(UserCreateRequest request, boolean isRegister){
        String email = request.getEmail();
        if(userRepository.findByEmail(email).isPresent()){
            throw new AppException(ErrorCode.EMAL_EXIST);
        }
        String phoneNumber = request.getPhoneNumber();
        if(userRepository.findByPhoneNumber(phoneNumber).isPresent()){
            throw new AppException(ErrorCode.PHONE_EXIST);
        }
        User u = userMapper.toUser(request);
        //mã hóa password người dùng - chỉ hash 1 lần từ plain text
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        u.setPasswordHash(encodedPassword);
        if(isRegister)
            u.setRole(RoleEnum.CUSTOMER);
        userRepository.save(u);
        return userMapper.toDto(u);
    }

    public UserResponse updateUser(int id, UserUpdateRequest request) {
        User u = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        String newEmail = request.getEmail();
        if(newEmail != null && !newEmail.isEmpty() && !newEmail.equals(u.getEmail()))
            if(userRepository.findByEmail(newEmail).isPresent())
                throw new AppException(ErrorCode.EMAL_EXIST);

        String newPhoneNumber = request.getPhoneNumber();
        if(newPhoneNumber != null && !newPhoneNumber.isEmpty() && !newPhoneNumber.equals(u.getPhoneNumber()))
            if(userRepository.findByPhoneNumber(newPhoneNumber).isPresent())
                throw new AppException(ErrorCode.PHONE_EXIST);

        userMapper.updateUser(u, request);
        userRepository.save(u);
        return userMapper.toDto(u);
    }
    public void deleteUser(int id){
        User u = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        userRepository.delete(u);
    }
}
