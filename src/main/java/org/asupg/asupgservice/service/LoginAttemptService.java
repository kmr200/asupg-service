package org.asupg.asupgservice.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    public final int maxAttempts;
    public final int lockDurationMinutes;

    private final ConcurrentHashMap<String, LoginAttemptData> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${asupg.login-attempts.max-retry}") int maxAttempts,
            @Value("${asupg.login-attempts.lock-duration-minutes}") int lockDurationMinutes
    ) {
        this.maxAttempts = maxAttempts;
        this.lockDurationMinutes = lockDurationMinutes;
    }

    public void recordFailure(String username) {
        attempts.merge(username, new LoginAttemptData(), (existing, newVal) -> {
            existing.increment();
            return existing;
        });
    }

    public void recordSuccess(String username) {
        attempts.remove(username);
    }

    public boolean isBlocked(String username) {
        LoginAttemptData attempt = attempts.get(username);
        if (attempt == null) return false;

        if (attempt.isExpired(lockDurationMinutes)) {
            attempts.remove(username);
            return false;
        }

        return attempt.getCount() >= maxAttempts;
    }

    public int getRemainingAttempts(String username) {
        LoginAttemptData attempt = attempts.get(username);
        if (attempt == null) return maxAttempts;
        return Math.max(0, maxAttempts - attempt.getCount());
    }

    @Data
    private static class LoginAttemptData {
        private int count = 1;
        private LocalDateTime lockedAt = LocalDateTime.now();

        public void increment() {
            count++;
            lockedAt = LocalDateTime.now();
        }

        public boolean isExpired(long lockDurationMinutes) {
            return lockedAt.plusMinutes(lockDurationMinutes).isBefore(LocalDateTime.now());
        }
    }

}
