package com.alibou.security.response;

import com.alibou.security.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private Role role;
    private String email;
    private boolean isActive;
    private String telephone;
    private String address;
    private String message;
}
