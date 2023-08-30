package com.alibou.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

//  @PostMapping("/register")
//  public ResponseEntity<AuthenticationResponse> register(
//      @RequestBody RegisterRequest request
//  ) {
//    return ResponseEntity.ok(service.register(request));
//  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    AuthenticationResponse response = service.register(request);

    if (Objects.equals(response.getMessage(), "Missing required fields. Please provide email, password, firstname, and lastname.")) {
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Objects.equals(response.getMessage(), "User already exists ! please login")) {
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }




    return new ResponseEntity<>(response, HttpStatus.OK);
  }





  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    AuthenticationResponse response = service.authenticate(request);

    if (Objects.equals(response.getMessage(), "User doesn't exist.")) {
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Objects.equals(response.getMessage(), "Your account has not been activated yet. We have sent verification email.")) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (Objects.equals(response.getMessage(), "Incorrect email or password.")) {
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(response,HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PostMapping("/verify-otp/{id}/otpCode/{code}")
  public ResponseEntity<?> verifyOtp(@PathVariable Long id, @PathVariable int code) {
    VerifyOtpResponse response = service.verifyOtp(id, code);

    if (Objects.equals(response.getMessage(), "OTP verification successful. User is now activated.")) {
      // Handle successful OTP verification and activation
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (Objects.equals(response.getMessage(), "You took so long to verify. please login again")) {
      // Handle case where OTP is expired
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Objects.equals(response.getMessage(), "User doesn't exist")) {
      // Handle case where user doesn't exist
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Objects.equals(response.getMessage(), "Invalid OTP. Please try again.")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

     response.setMessage("Server error");
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @PostMapping("/resend-otp/{id}")
  public ResponseEntity<?> resendOtp(@PathVariable Long id){
    VerifyOtpResponse response=service.resendOtp(id);
    if (Objects.equals(response.getMessage(), "User doesn't exist")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/forgot-password/{email}")
  public ResponseEntity<?> forgotPassword(@PathVariable String email){
    AuthenticationResponse response=service.forgotPassword(email);
    if (Objects.equals(response.getMessage(), "User doesn't exist")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword (@RequestBody ResetPasswordRequest resetPasswordRequest){
    ResetPasswordResponse response = service.resetPassword(resetPasswordRequest);

    if (Objects.equals(response.getMessage(), "All fields are required!")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    if (Objects.equals(response.getMessage(), "User doesn't exist")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    if (Objects.equals(response.getMessage(), "You took so long to verify. please try again")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Objects.equals(response.getMessage(), "Your password reset successfully!")) {
      // Handle case where OTP is invalid
      response.setMessage("Your password was reset");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (Objects.equals(response.getMessage(), "Invalid OTP. Please try again.")) {
      // Handle case where OTP is invalid
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    response.setMessage("Server Error");
    return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

  }







}
