package com.example.mentalhealth.screening.repository;

import com.example.mentalhealth.screening.domain.ScreeningSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisScreeningSessionRepository implements ScreeningSessionRepository {

    private static final String KEY_PREFIX = "screening:session:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, ScreeningSession> sessionRedisTemplate;

    /**
     * Save or update screening session
     */
    @Override
    public ScreeningSession save(ScreeningSession session) {

        if (session == null || session.getSessionId() == null) {
            throw new IllegalArgumentException("ScreeningSession or sessionId cannot be null");
        }

        String key = buildKey(session.getSessionId());

        try {

            sessionRedisTemplate
                    .opsForValue()
                    .set(key, session, TTL);

            log.debug(
                    "Saved screening session | sessionId={} correlationId={} ttl={}s",
                    session.getSessionId(),
                    session.getCorrelationId(),
                    TTL.toSeconds()
            );

            return session;

        } catch (Exception ex) {

            log.error(
                    "Failed to save screening session | sessionId={}",
                    session.getSessionId(),
                    ex
            );

            throw new IllegalStateException("Redis session save failed", ex);
        }
    }

    /**
     * Fetch screening session
     */
    @Override
    public Optional<ScreeningSession> findById(String sessionId) {

        if (sessionId == null) {
            return Optional.empty();
        }

        String key = buildKey(sessionId);

        try {

            ScreeningSession session =
                    sessionRedisTemplate
                            .opsForValue()
                            .get(key);

            if (session != null) {

                log.debug(
                        "Screening session found | sessionId={} correlationId={}",
                        sessionId,
                        session.getCorrelationId()
                );

                // Optional: refresh TTL on access
                sessionRedisTemplate.expire(key, TTL);

            } else {

                log.warn(
                        "Screening session not found | sessionId={}",
                        sessionId
                );
            }

            return Optional.ofNullable(session);

        } catch (Exception ex) {

            log.error(
                    "Redis fetch failed | sessionId={}",
                    sessionId,
                    ex
            );

            throw new IllegalStateException("Redis session fetch failed", ex);
        }
    }

    /**
     * Delete screening session
     */
    @Override
    public void delete(String sessionId) {

        if (sessionId == null) {
            return;
        }

        String key = buildKey(sessionId);

        try {

            sessionRedisTemplate.delete(key);

            log.debug(
                    "Deleted screening session | sessionId={}",
                    sessionId
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to delete screening session | sessionId={}",
                    sessionId,
                    ex
            );

            throw new IllegalStateException("Redis session delete failed", ex);
        }
    }

    /**
     * Build Redis key
     */
    private String buildKey(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}