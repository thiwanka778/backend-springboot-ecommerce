package com.alibou.security.models;

import com.alibou.security.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int otpCode;
    private Date createdDateAndTime;
    private Date expiredDateAndTime;

    @ManyToOne
    @JoinColumn(name = "user_id") // Define the foreign key column
    private User user;
}
