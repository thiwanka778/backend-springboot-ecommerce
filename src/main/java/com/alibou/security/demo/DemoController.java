package com.alibou.security.demo;

import com.alibou.security.config.JwtService;
import com.alibou.security.config.JwtTokenExtractor;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/demo-controller")
@Hidden
public class DemoController {

  @Autowired
  JwtTokenExtractor tokenExtractor;

  @Autowired
  UserRepository userRepository;

  @GetMapping
  public ResponseEntity<String> sayHello(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String userEmail = tokenExtractor.extractUserEmail(token);

    User user = new User();

    if (userEmail != null) {
      Optional<User> userOptional=userRepository.findByEmail(userEmail);
      if (userOptional.isPresent()) {
        user = userOptional.get();

      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");
      }

    }
    if(!user.getEmail().isEmpty()){
      return new ResponseEntity<>("User found "+user.getEmail(),HttpStatus.OK);
    }



    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Authentication failed. Please provide a valid token.");
  }

}
