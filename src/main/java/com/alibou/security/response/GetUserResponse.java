package com.alibou.security.response;

import com.alibou.security.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponse {
    private String email;
    private Long id;
    private String firstname;
    private String lastname;
    private Role role;
    private boolean isActive;
    private String message;

}
