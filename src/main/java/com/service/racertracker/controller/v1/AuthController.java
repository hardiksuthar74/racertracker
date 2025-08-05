package com.service.racertracker.controller.v1;


import com.service.racertracker.dto.request.AuthRequest;
import com.service.racertracker.dto.request.OtpRequest;
import com.service.racertracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        String message = authService.loginOrCreateUser(authRequest.getEmail());

        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid OtpRequest otpRequest) {
        Map<String,String> tokens = authService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody @Valid AuthRequest authRequest) {
        String message = authService.resendOtp(authRequest.getEmail());
        return ResponseEntity.ok(Map.of("message", message));
    }
}
