package com.alibou.security.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    private String email;
    private int otpCode;
    private String password;
}
