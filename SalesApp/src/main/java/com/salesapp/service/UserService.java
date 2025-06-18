package com.salesapp.service;

import com.salesapp.dto.request.UserCreateRequest;
import com.salesapp.dto.request.UserUpdateRequest;
import com.salesapp.dto.response.CategoryResponse;
import com.salesapp.dto.response.UserResponse;
import com.salesapp.entity.Category;
import com.salesapp.entity.User;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.mapper.UserMapper;
import com.salesapp.repository.CartRepository;
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

    public UserResponse createUser(UserCreateRequest request){
        String email = request.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new AppException(ErrorCode.EMAL_EXIST);
        }
        String phoneNumber = request.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new AppException(ErrorCode.PHONE_EXIST);
        }
        User u = userMapper.toUser(request);
        //mã hóa password người dùng
        u.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        userRepository.save(u);
        return userMapper.toDto(u);
    }

    public UserResponse updateUser(int id, UserUpdateRequest request) {
        User u = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        String email = request.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new AppException(ErrorCode.EMAL_EXIST);
        }
        String phoneNumber = request.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new AppException(ErrorCode.PHONE_EXIST);
        }
        userMapper.updateUser(u, request);
        userRepository.save(u);
        return userMapper.toDto(u);
    }
    public void deleteUser(int id){
        User u = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        userRepository.delete(u);
    }
}
