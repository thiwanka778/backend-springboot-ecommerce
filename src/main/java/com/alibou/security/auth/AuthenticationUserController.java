package com.alibou.security.auth;

import com.alibou.security.response.GetUserResponse;
import com.alibou.security.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class AuthenticationUserController {
    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/me")
    public ResponseEntity<?> getUser(HttpServletRequest request){
        GetUserResponse response=authenticationService.getUser(request);

        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User found")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
