package com.example.mentalhealth.config;

import com.example.mentalhealth.session.SessionService;
import com.example.mentalhealth.session.SessionState;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, SessionState> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, SessionState> template = new RedisTemplate<>();
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