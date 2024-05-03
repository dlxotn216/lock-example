package io.taesu.lockexample.application

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * Created by taesu on 2024/05/03.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@ConditionalOnProperty(
    value = ["app.lock"],
    havingValue = "redisson",
    matchIfMissing = false
)
@Service
class RedissonDistributedLockService(
    private val redissonClient: RedissonClient
): DistributedLockService {
    override fun tryJobWithLock(lockContext: LockContext, job: () -> Any?): Any? {
        val rLock = redissonClient.getLock(lockContext.key)

        try {
            if (!rLock.tryLock(lockContext.waitTime.toLong(), lockContext.lockTimeout.toLong(), lockContext.timeUnit)) {
                throw IllegalStateException("Fail to acquire lock")
            }

            return job()
        } catch (e: InterruptedException) {
            throw InterruptedException()
        } finally {
            tryUnlock(rLock, lockContext)
        }
    }

    private fun tryUnlock(rLock: RLock, lock: LockContext) {
        try {
            rLock.unlock()
        } catch (e: IllegalMonitorStateException) {
            log.info("Fail to unlock [{}]", lock.key)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}
