package com.example.mentalhealth.screening.config;


import com.example.mentalhealth.screening.domain.ScreeningSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ScreeningSession> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, ScreeningSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> valueSerializer = RedisSerializer.json();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);       // ✅
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(RedisSerializer.json());   // ✅

        template.afterPropertiesSet();
        return template;
    }
}