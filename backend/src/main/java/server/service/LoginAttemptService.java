package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MINUTES = 5;

    private String getKey(String email) {
        return "login:fail:" + email;
    }

    public void loginSucceeded(String email) {
        redisTemplate.delete(getKey(email));
    }

    public void loginFailed(String email) {
        String key = getKey(email);
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts == 1) {
            // 처음 실패한 경우 블록 타이머 설정
            redisTemplate.expire(key, BLOCK_TIME_MINUTES, TimeUnit.MINUTES);
        }
    }

    public boolean isBlocked(String email) {
        String key = getKey(email);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return false;
        return Integer.parseInt(value) >= MAX_ATTEMPTS;
    }
}
