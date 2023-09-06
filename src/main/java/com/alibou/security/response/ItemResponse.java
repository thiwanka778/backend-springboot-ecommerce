package com.alibou.security.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemResponse {

    private String message;
    private List<ItemResponseObject> itemList;
    private Long totalItems;
}
