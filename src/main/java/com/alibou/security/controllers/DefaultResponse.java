package com.alibou.security.controllers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultResponse {
    private String message;
}
