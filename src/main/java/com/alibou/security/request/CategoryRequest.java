package com.alibou.security.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {

    private String title;
    private String des;
    private String imageUrl;
}
