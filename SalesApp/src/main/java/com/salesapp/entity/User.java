package com.salesapp.entity;

import com.salesapp.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", nullable = false)
    private Integer id;

    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Email", nullable = false, length = 100)
    private String email;

    @Column(name = "PhoneNumber", length = 15)
    private String phoneNumber;

    @Column(name = "Address")
    private String address;

    @Column(name = "Role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @OneToMany(mappedBy = "userID")
    private Set<Cart> carts = new LinkedHashSet<>();

    // ✅ Danh sách tin nhắn gửi
    @OneToMany(mappedBy = "userID")
    private Set<ChatMessage> sentMessages = new LinkedHashSet<>();

    // ✅ Danh sách tin nhắn nhận
    @OneToMany(mappedBy = "receiver")
    private Set<ChatMessage> receivedMessages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userID")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userID")
    private Set<Order> orders = new LinkedHashSet<>();
}
