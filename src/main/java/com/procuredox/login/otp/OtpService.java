package com.procuredox.login.otp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final Duration VALIDITY = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 3;

    private final SecureRandom random = new SecureRandom();
    private final StringRedisTemplate redis;

    public OtpService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String codeKey(String email) {
        return "otp:code:" + email;
    }

    private String attemptsKey(String email) {
        return "otp:attempts:" + email;
    }

    public void generate(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        redis.opsForValue().set(codeKey(email), code, VALIDITY);
        redis.delete(attemptsKey(email));
        log.info("OTP for {} is {} (valid 5 minutes)", email, code);
    }

    public OtpResult verify(String email, String input) {
        String saved = redis.opsForValue().get(codeKey(email));
        if (saved == null) return OtpResult.NO_CODE;

        String typed = input == null ? "" : input.trim();
        boolean match = MessageDigest.isEqual(
                saved.getBytes(StandardCharsets.UTF_8),
                typed.getBytes(StandardCharsets.UTF_8));

        if (match) {
            redis.delete(List.of(codeKey(email), attemptsKey(email)));
            return OtpResult.OK;
        }

        Long attempts = redis.opsForValue().increment(attemptsKey(email));
        if (attempts != null && attempts == 1) {
            redis.expire(attemptsKey(email), VALIDITY);
        }
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redis.delete(List.of(codeKey(email), attemptsKey(email)));
            return OtpResult.TOO_MANY_ATTEMPTS;
        }
        return OtpResult.WRONG;
    }
}