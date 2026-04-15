package com.quodex._miles.service.Impl;

import com.quodex._miles.config.RedisProperties;
import com.quodex._miles.io.RedisInsertRequest;
import com.quodex._miles.io.RedisInsertResponse;
import com.quodex._miles.io.RedisValueResponse;
import com.quodex._miles.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public RedisInsertResponse insert(RedisInsertRequest request) {
        if (!redisProperties.isEnabled()) {
            throw new IllegalStateException("Redis is not enabled. Set app.redis.enabled=true");
        }

        if (request == null || !StringUtils.hasText(request.getKey())) {
            throw new IllegalArgumentException("Redis key is required");
        }
        if (!StringUtils.hasText(request.getValue())) {
            throw new IllegalArgumentException("Redis value is required");
        }

        long ttlSeconds = request.getTtlSeconds() != null
                ? request.getTtlSeconds()
                : redisProperties.getTtlSeconds();

        stringRedisTemplate.opsForValue()
                .set(request.getKey(), request.getValue(), Duration.ofSeconds(ttlSeconds));

        RedisInsertResponse response = new RedisInsertResponse();
        response.setKey(request.getKey());
        response.setValue(request.getValue());
        response.setTtlSeconds(ttlSeconds);
        response.setStatus("STORED");
        return response;
    }

    @Override
    public RedisValueResponse getByKey(String key) {
        if (!redisProperties.isEnabled()) {
            throw new IllegalStateException("Redis is not enabled. Set app.redis.enabled=true");
        }
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Redis key is required");
        }

        String value = stringRedisTemplate.opsForValue().get(key);
        RedisValueResponse response = new RedisValueResponse();
        response.setKey(key);
        response.setValue(value);
        return response;
    }
}
