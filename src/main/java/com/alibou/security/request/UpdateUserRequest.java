package com.alibou.security.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private String firstname;
    private String lastname;
    private String telephone;
    private String address;
}
