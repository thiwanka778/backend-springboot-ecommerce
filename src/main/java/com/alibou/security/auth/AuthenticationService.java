package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.config.JwtTokenExtractor;
import com.alibou.security.models.Otp;
import com.alibou.security.repositories.OtpRepository;
import com.alibou.security.response.GetUserResponse;
import com.alibou.security.response.Response;
import com.alibou.security.token.Token;
import com.alibou.security.token.TokenRepository;
import com.alibou.security.token.TokenType;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  @Autowired
  private OtpRepository otpRepository;

  @Autowired
  JwtTokenExtractor tokenExtractor;

  public AuthenticationResponse register(RegisterRequest request) {
    int otpCode = generateRandomSixDigitNumber();
    Otp otp = new Otp();
    otp.setOtpCode(otpCode);
    otp.setCreatedDateAndTime(new Date());

    // Calculate the expiration date and time as 10 minutes from now
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, 1); // Add 10 minutes
    otp.setExpiredDateAndTime(calendar.getTime());

    if(request.getEmail()==null|| request.getEmail().isEmpty()||
    request.getPassword()==null|| request.getPassword().isEmpty()||
    request.getFirstname()==null|| request.getFirstname().isEmpty()||
    request.getLastname()==null|| request.getLastname().isEmpty()){
      return AuthenticationResponse.builder()
              .message("Missing required fields. Please provide email, password, firstname and lastname.")
              .build();
    }

    if (repository.existsByEmail(request.getEmail())) {
      Optional<User> existingUserOptional = repository.findByEmail(request.getEmail());

      if (existingUserOptional.isPresent()) {
        return AuthenticationResponse.builder()
                .message("User already exists ! please login")
                .build();

      }
    }


    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);




    // Set the user for the OTP
    otp.setUser(savedUser);

    // Save the OTP in the database
    otpRepository.save(otp);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .message("We have sent verification email.")
            .id(savedUser.getId())
            .firstname(savedUser.getFirstname())
            .lastname(savedUser.getLastname())
            .email(savedUser.getEmail())
            .role(savedUser.getRole())
            .isActive(savedUser.isActive())

        .build();
  }

  public static int generateRandomSixDigitNumber() {
    // Create an instance of the Random class
    Random random = new Random();

    // Generate a random number between 100,000 and 999,999
    int min = 100000;
    int max = 999999;
    return random.nextInt(max - min + 1) + min;
  }




  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    Optional<User> userOptional = repository.findByEmail(request.getEmail());

    if (userOptional.isEmpty()) {
      // User doesn't exist
      return AuthenticationResponse.builder()
              .message("User doesn't exist.")
              .build();
    }
    User existingUser = userOptional.get();

    if (!existingUser.isActive()) {
      int otpCode = generateRandomSixDigitNumber();
      Otp otp = new Otp();
      otp.setOtpCode(otpCode);
      otp.setCreatedDateAndTime(new Date());

      // Calculate the expiration date and time as 10 minutes from now
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.MINUTE, 1); // Add 10 minutes
      otp.setExpiredDateAndTime(calendar.getTime());
      List<Otp> existingOtps = otpRepository.findByUser(existingUser);

      // Delete existing OTPs
      if (!existingOtps.isEmpty()) {
        // Delete existing OTPs
        otpRepository.deleteAll(existingOtps);
      }

      otp.setUser(existingUser);

      // Save the OTP in the database
      otpRepository.save(otp);

      // User is not active
      return AuthenticationResponse.builder()
              .message("Your account has not been activated yet. We have sent verification email.")
              .id(existingUser.getId())
              .firstname(existingUser.getFirstname())
              .lastname(existingUser.getLastname())
              .email(existingUser.getEmail())
              .isActive(existingUser.isActive())
              .role(existingUser.getRole())
              .build();
    }

    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
    } catch (AuthenticationException e) {
      // Incorrect email or password
      return AuthenticationResponse.builder()
              .message("Incorrect email or password.")
              .build();
    }

    User user = userOptional.get();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .isActive(user.isActive())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .id(user.getId())
            .role(user.getRole())
            .email(user.getEmail())
            .message("Login Successfully!")
            .build();
  }




  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public VerifyOtpResponse verifyOtp(Long id, int otp) {
    Optional<User> userOptional = repository.findById(id);

    if (userOptional.isEmpty()) {
      return VerifyOtpResponse.builder()
              .message("User doesn't exist")
              .build();
    }

    User existingUser = userOptional.get();
    List<Otp> existingOtps = otpRepository.findByUser(existingUser);

    if (existingOtps.isEmpty()) {
      // Delete existing OTPs
      return VerifyOtpResponse.builder()
              .message("You took so long to verify. please login again")
              .build();
    }

    // Assuming the OTP code is valid for 10 minutes (adjust this as needed)
    long otpValidityDurationMs = 1 * 60 * 1000; // 10 minutes in milliseconds
    Date currentDateTime = new Date();

    boolean validOtpFound = false;
    boolean expiredOtpFound = false;

    // Check each existing OTP
    for (Otp existingOtp : existingOtps) {
      Date otpExpirationTime = existingOtp.getExpiredDateAndTime();

      if (currentDateTime.before(otpExpirationTime) && existingOtp.getOtpCode() == otp) {
        // OTP is not expired and matches the provided OTP
        existingUser.setActive(true); // Set user as active
        repository.save(existingUser); // Update user's isActive status
        otpRepository.delete(existingOtp); // Delete the OTP
        validOtpFound = true;
        break; // Exit the loop once a valid OTP is found
      } else if (currentDateTime.after(otpExpirationTime)) {
        // OTP is expired
        expiredOtpFound = true;
      }
    }

    if (validOtpFound) {
      if (!existingOtps.isEmpty()) {
        // Delete existing OTPs
        otpRepository.deleteAll(existingOtps);
      }
      return VerifyOtpResponse.builder()
              .message("OTP verification successful. User is now activated.")
              .build();
    } else if (expiredOtpFound) {
      if (!existingOtps.isEmpty()) {
        // Delete existing OTPs
        otpRepository.deleteAll(existingOtps);
      }
      return VerifyOtpResponse.builder()
              .message("You took so long to verify. please login again")
              .build();
    } else {
      // No valid OTP found
      return VerifyOtpResponse.builder()
              .message("Invalid OTP. Please try again.")
              .build();
    }
  }


  public VerifyOtpResponse resendOtp(Long id) {
    Optional<User> userOptional = repository.findById(id);

    if (userOptional.isEmpty()) {
      return VerifyOtpResponse.builder()
              .message("User doesn't exist")
              .build();
    }
    User existingUser = userOptional.get();
    List<Otp> existingOtps = otpRepository.findByUser(existingUser);
    if (!existingOtps.isEmpty()) {
      // Delete existing OTPs
      otpRepository.deleteAll(existingOtps);
    }

    int otpCode = generateRandomSixDigitNumber();
    Otp otp = new Otp();
    otp.setOtpCode(otpCode);
    otp.setCreatedDateAndTime(new Date());

    // Calculate the expiration date and time as 10 minutes from now
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, 1); // Add 10 minutes
    otp.setExpiredDateAndTime(calendar.getTime());
    otp.setUser(existingUser);

    // Save the OTP in the database
    otpRepository.save(otp);
    return VerifyOtpResponse.builder()
            .message("We have sent otp code again")
            .build();


  }

  public AuthenticationResponse forgotPassword(String email) {
    Optional<User> userOptional = repository.findByEmail(email);

    if (userOptional.isEmpty()) {
      return AuthenticationResponse.builder()
              .message("User doesn't exist")
              .build();
    }
    User existingUser = userOptional.get();
    List<Otp> existingOtps = otpRepository.findByUser(existingUser);

    if (!existingOtps.isEmpty()) {
      // Delete existing OTPs
      otpRepository.deleteAll(existingOtps);
    }

    int otpCode = generateRandomSixDigitNumber();
    Otp otp = new Otp();
    otp.setOtpCode(otpCode);
    otp.setCreatedDateAndTime(new Date());

    // Calculate the expiration date and time as 10 minutes from now
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, 1); // Add 10 minutes
    otp.setExpiredDateAndTime(calendar.getTime());
    otp.setUser(existingUser);

    // Save the OTP in the database
    otpRepository.save(otp);

    return AuthenticationResponse.builder()
            .id(existingUser.getId())
            .firstname(existingUser.getFirstname())
            .lastname(existingUser.getLastname())
            .email(existingUser.getEmail())
            .isActive(existingUser.isActive())
            .role(existingUser.getRole())
            .message("We have sent verification code")
            .build();

  }

  public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
if(resetPasswordRequest.getEmail()==null || resetPasswordRequest.getEmail().isEmpty()||
resetPasswordRequest.getPassword()==null|| resetPasswordRequest.getPassword().isEmpty()
){
  return ResetPasswordResponse.builder()
          .message("All fields are required!")
          .build();

}
      Optional<User> existingUserOptional = repository.findByEmail(resetPasswordRequest.getEmail());
    if(existingUserOptional.isEmpty()){
     return ResetPasswordResponse.builder()
           .message("User doesn't exist")
           .build();
     }

    User existingUser = existingUserOptional.get();

    List<Otp> existingOtps = otpRepository.findByUser(existingUser);
    if (existingOtps.isEmpty()) {
      // Delete existing OTPs
      return ResetPasswordResponse.builder()
              .message("You took so long to verify. please try again")
              .build();
    }
    // Assuming the OTP code is valid for 10 minutes (adjust this as needed)
    long otpValidityDurationMs = 1 * 60 * 1000; // 1 minutes in milliseconds
    Date currentDateTime = new Date();

    boolean validOtpFound = false;
    boolean expiredOtpFound = false;
    for (Otp existingOtp : existingOtps) {
      Date otpExpirationTime = existingOtp.getExpiredDateAndTime();

      if (currentDateTime.before(otpExpirationTime) && existingOtp.getOtpCode() == resetPasswordRequest.getOtpCode()) {
        // OTP is not expired and matches the provided OTP
//        existingUser.setActive(true);
        existingUser.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));// Set user as active
        repository.save(existingUser); // Update user's isActive status
        otpRepository.delete(existingOtp); // Delete the OTP
        validOtpFound = true;
        break; // Exit the loop once a valid OTP is found
      } else if (currentDateTime.after(otpExpirationTime)) {
        // OTP is expired
        expiredOtpFound = true;
      }
    }

    if (validOtpFound) {
      if (!existingOtps.isEmpty()) {
        // Delete existing OTPs
        otpRepository.deleteAll(existingOtps);
      }
      return ResetPasswordResponse.builder()
              .message("Your password reset successfully!")
              .build();
    } else if (expiredOtpFound) {
      if (!existingOtps.isEmpty()) {
        // Delete existing OTPs
        otpRepository.deleteAll(existingOtps);
      }
      return ResetPasswordResponse.builder()
              .message("You took so long to verify. please try again")
              .build();
    } else {
      // No valid OTP found
      return ResetPasswordResponse.builder()
              .message("Invalid OTP. Please try again.")
              .build();
    }



  }

  public GetUserResponse getUser(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String userEmail = tokenExtractor.extractUserEmail(token);
    User user = new User();
    if (userEmail != null) {
      Optional<User> userOptional=repository.findByEmail(userEmail);
      if (userOptional.isPresent()) {
        user = userOptional.get();

      } else {
        return GetUserResponse.builder()
                .message("User not found")
                .build();
      }

    }
    if(user.getEmail().isEmpty()){
      return GetUserResponse.builder()
              .message("User not found")
              .build();
    }
    if(!user.isActive()){
      return GetUserResponse.builder()
              .message("User not activated")
              .build();
    }

    return GetUserResponse.builder()
            .message("User found")
            .id(user.getId())
            .email(user.getEmail())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .role(user.getRole())
            .isActive(user.isActive())
            .build();


  }
}
