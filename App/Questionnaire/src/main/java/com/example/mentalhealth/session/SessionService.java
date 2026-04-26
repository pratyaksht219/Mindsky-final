package com.example.mentalhealth.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SessionService implements SessionStore {

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, SessionState> redisTemplate;

    @Override
    public SessionState getOrCreateSession(String sessionId) {

        String key = buildKey(sessionId);

        SessionState session = redisTemplate.opsForValue().get(key);

        if (session == null) {
            session = new SessionState(sessionId);
            save(session);
        }

        return session;
    }

    @Override
    public void save(SessionState session) {

        String key = buildKey(session.getSessionId());

        redisTemplate
                .opsForValue()
                .set(key, session, SESSION_TTL);
    }

    @Override
    public void delete(String sessionId) {
        redisTemplate.delete(buildKey(sessionId));
    }



    private String buildKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }
}