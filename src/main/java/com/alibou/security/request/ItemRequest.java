package com.alibou.security.request;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ItemRequest {
    private String title;
    private String des;
    private BigDecimal price;
    private int stockQuantity;
    private List<String> imageArray;
    private Long categoryId;
}
