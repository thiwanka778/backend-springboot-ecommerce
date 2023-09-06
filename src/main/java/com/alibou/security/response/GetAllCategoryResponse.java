package com.alibou.security.response;

import com.alibou.security.models.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class GetAllCategoryResponse {
    private List<Category> categoryList;
    private String message;
}
