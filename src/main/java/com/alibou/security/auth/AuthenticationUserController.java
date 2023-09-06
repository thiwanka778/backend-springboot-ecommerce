package com.alibou.security.auth;

import com.alibou.security.request.UpdateUserRequest;
import com.alibou.security.response.GetUserResponse;
import com.alibou.security.response.Response;
import com.alibou.security.response.UpdateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
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

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @RequestBody UpdateUserRequest updateUserRequest){
        UpdateUserResponse response=authenticationService.updateUser(request,updateUserRequest);

        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (Objects.equals(response.getMessage(), "Phone number already exist in the database")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        if (Objects.equals(response.getMessage(), "Failed to update user")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User updated successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
