package com.salesapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {
    @Id
    @Size(max = 255)
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "ExpiryTime")
    private Date expiryTime;

}