package com.demo.server.im;

import com.demo.server.shortlinkserver;
import com.demo.mapper.short_linkmapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class shortlinkserverim implements shortlinkserver {

    @Autowired
    private short_linkmapper shortLinkMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "short:";
    private static final String NULL_PLACEHOLDER = "NULL";

    @Override
    public String getLongUrl(String shortCode) {
        // 1. 先查 Redis
        String cacheKey = CACHE_PREFIX + shortCode;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

        if (cachedUrl != null) {
            if (NULL_PLACEHOLDER.equals(cachedUrl)) {
                throw new RuntimeException("短链接不存在");
            }
            System.out.println("✅ Redis 命中缓存: " + shortCode);
            return cachedUrl;
        }

        System.out.println("❌ Redis 未命中，查数据库: " + shortCode);

        // 2. Redis 没有，查 MySQL
        String longUrl = shortLinkMapper.getLongUrlByShortCode(shortCode);
        if (longUrl == null) {
            // 缓存空值，防止缓存穿透
            redisTemplate.opsForValue().set(cacheKey, NULL_PLACEHOLDER, 1, java.util.concurrent.TimeUnit.MINUTES);
            throw new RuntimeException("短链接不存在");
        }

        // 3. 写入 Redis（30 分钟过期）
        redisTemplate.opsForValue().set(cacheKey, longUrl, 30, java.util.concurrent.TimeUnit.MINUTES);

        return longUrl;
    }

}

   
