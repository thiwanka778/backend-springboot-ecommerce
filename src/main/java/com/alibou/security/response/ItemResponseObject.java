package com.alibou.security.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ItemResponseObject {

    private Long id;
    private String title;
    private String des;
    private BigDecimal price;
    private boolean isDelete;
    private int stockQuantity;
    private Long userId;
    private Long categoryId;
    private List<String> imageArray;
}
