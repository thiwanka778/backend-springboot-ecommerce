package com.alibou.security.models;


import com.alibou.security.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String des;
    private String imageUrl;
    private boolean isDeleted=false;
//    @ManyToOne
//    @JoinColumn(name="user_id")
    private Long userId;
}
