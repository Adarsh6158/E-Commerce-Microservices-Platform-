package com.ecommerce.inventory_service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Component
public class DistributedLockService {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockService.class);
    private static final String LOCK_PREFIX = "lock:inventory:";
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY = Duration.ofMillis(100);

    private static final String RELEASE_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "  return redis.call('del', KEYS[1]) " +
                    "else " +
                    "  return 0 " +
                    "end";

    private final ReactiveStringRedisTemplate redisTemplate;

    public DistributedLockService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<String> acquireLock(String resourceId) {
        String lockKey = LOCK_PREFIX + resourceId;
        String lockValue = UUID.randomUUID().toString();

        return tryAcquire(lockKey, lockValue, 0)
                .doOnSuccess(v -> {
                    if (v != null) {
                        log.debug("Lock acquired. resource={}, lockId={}", resourceId, v);
                    }
                });
    }

    public Mono<Boolean> releaseLock(String resourceId, String lockValue) {
        String lockKey = LOCK_PREFIX + resourceId;
        RedisScript<Long> script = RedisScript.of(RELEASE_SCRIPT, Long.class);

        return redisTemplate.execute(
                        script,
                        Collections.singletonList(lockKey),
                        Collections.singletonList(lockValue)
                )
                .next()
                .map(result -> result > 0)
                .defaultIfEmpty(false)
                .doOnSuccess(released -> {
                    if (released) {
                        log.debug("Lock released. resource={}", resourceId);
                    } else {
                        log.warn("Lock release failed (not owner or expired). resource={}", resourceId);
                    }
                });
    }

    private Mono<String> tryAcquire(String lockKey, String lockValue, int attempt) {
        if (attempt >= MAX_RETRIES) {
            log.warn("Failed to acquire lock after {} attempts. key={}", MAX_RETRIES, lockKey);
            return Mono.empty();
        }

        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, LOCK_TTL)
                .flatMap(acquired -> {
                    if (Boolean.TRUE.equals(acquired)) {
                        return Mono.just(lockValue);
                    }

                    return Mono.delay(RETRY_DELAY.multipliedBy(attempt + 1))
                            .then(tryAcquire(lockKey, lockValue, attempt + 1));
                });
    }
}
