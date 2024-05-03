package io.taesu.lockexample.application

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Created by taesu on 2024/05/03.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@ConditionalOnProperty(
    value = ["app.lock"],
    havingValue = "redis",
    matchIfMissing = false
)
@Service
class RedisSimpleDistributedLockService(
    private val stringRedisTemplate: StringRedisTemplate
): DistributedLockService {
    override fun tryJobWithLock(lockContext: LockContext, job: () -> Any?): Any? {
        val startedAt = LocalDateTime.now()
        while (!tryLock(lockContext)) {
            // spin lock
            Thread.sleep(100)
            val now = LocalDateTime.now()
            if (ChronoUnit.SECONDS.between(startedAt, now) > lockContext.waitTime) {
                throw IllegalStateException("Fail to acquire lock")
            }
        }
        return try {
            job()
        } finally {
            unlock(lockContext)
        }
    }

    private fun tryLock(lockContext: LockContext): Boolean {
        return stringRedisTemplate.opsForValue()
            .setIfAbsent(lockContext.key, "1", lockContext.lockTimeout.toLong(), lockContext.timeUnit)
            ?: false
    }

    private fun unlock(lockContext: LockContext) {
        stringRedisTemplate.delete(lockContext.key)
    }
}
