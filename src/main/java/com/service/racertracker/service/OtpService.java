package com.service.racertracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    public void storeOtp(String email, String otp, long expirationMinutes) {
        redisTemplate.opsForValue().set(email, otp, expirationMinutes, TimeUnit.MINUTES);
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }

}
