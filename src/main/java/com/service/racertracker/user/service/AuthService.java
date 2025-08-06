package com.service.racertracker.user.service;

import com.service.racertracker.user.model.User;
import com.service.racertracker.user.repository.UserRepository;
import com.service.racertracker.core.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private String generateOtpAndSendEmail(String email) {
        // Generate random 6 digit otp
        String otp = generateOtp();
        otpService.storeOtp(email, otp,5);
        emailService.sendOtpEmail(email, otp);

        return "OTP sent to email";
    }

    @Transactional
    public String loginOrCreateUser(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .emailVerified(false)
                    .build();

            return userRepository.save(newUser);
        });

        return generateOtpAndSendEmail(user.getEmail());
    }

    @Transactional
    public String resendOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        otpService.deleteOtp(email);

        if (userOptional.isEmpty()) {
            return loginOrCreateUser(email);
        }

        return generateOtpAndSendEmail(email);
    }

    @Transactional
    public Map<String, String> verifyOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        String correctOtp = otpService.getOtp(email);
        if (correctOtp == null || !correctOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        User user = userOptional.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        otpService.deleteOtp(email);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), Map.of());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

    }


    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
