package com.service.racertracker.service;

import com.service.racertracker.db.model.User;
import com.service.racertracker.repository.UserRepository;
import com.service.racertracker.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Map<String, String> otpStore = new HashMap<>();

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private String generateOtpAndSendEmail(String email) {
        // Generate random 6 digit otp
        String otp = generateOtp();
        otpStore.put(email, otp);
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

        String correctOtp = otpStore.get(email);
        if (correctOtp == null || !correctOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        User user = userOptional.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        otpStore.remove(email);

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
