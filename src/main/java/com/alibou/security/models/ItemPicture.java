package com.alibou.security.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table
public class ItemPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long itemId;
    private String url;

}
