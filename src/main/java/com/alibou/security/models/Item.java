package com.alibou.security.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String des;
    private BigDecimal price;
    private boolean isDeleted=false;
    private int stockQuantity;
    private Long userId;
    private Long categoryId;

}
