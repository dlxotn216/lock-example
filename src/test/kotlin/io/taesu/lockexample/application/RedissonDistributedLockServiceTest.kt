package io.taesu.lockexample.application

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by taesu on 2024/05/03.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@SpringBootTest
@ActiveProfiles("test")
class RedissonDistributedLockServiceTest {
    @Autowired
    private lateinit var redissonDistributedLockService: RedissonDistributedLockService

    @Test
    fun `Redisson lock 획득 성공 테스트`() {
        // given
        val lockContext = LockContext(
            key = "lock:1",
            waitTime = 3,
            lockTimeout = 2,
            timeUnit = TimeUnit.SECONDS
        )

        // when
        val jobResult = redissonDistributedLockService.tryJobWithLock(lockContext) {
            "success"
        }

        // then
        Assertions.assertThat(jobResult).isEqualTo("success")
    }

    @Test
    fun `Redisson lock wait timeout 테스트`() {
        // given
        val lockContext = LockContext(
            key = "lock:2",
            waitTime = 3,
            lockTimeout = 10,   // 10초 후 만료
            timeUnit = TimeUnit.SECONDS
        )
        Executors.newSingleThreadExecutor().submit {
            redissonDistributedLockService.tryJobWithLock(lockContext) {
                Thread.sleep(Duration.of(10, ChronoUnit.SECONDS))   // 10초 작업 수행
            }
        }
        Thread.sleep(100) // executors가 먼저 실행되도록

        // when
        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            redissonDistributedLockService.tryJobWithLock(lockContext) {
                "success"
            }
        }

        // then3
        Assertions.assertThat(exception.message).isEqualTo("Fail to acquire lock")
    }

    @Test
    fun `Redis Spinlock 락 lock timeout 테스트`() {
        // given
        val lockContext = LockContext(
            key = "lock:3",
            waitTime = 3,
            lockTimeout = 3,   // 3초 후 만료
            timeUnit = TimeUnit.SECONDS
        )
        val future = Executors.newSingleThreadExecutor().submit(Callable {
            redissonDistributedLockService.tryJobWithLock(lockContext) {
                Thread.sleep(Duration.of(10, ChronoUnit.SECONDS))   // 10초 작업 수행
                "success"
            }
        })
        Thread.sleep(100) // executors가 먼저 실행되도록

        // when
        val jobResult = redissonDistributedLockService.tryJobWithLock(lockContext) {
            "success"
        }

        // then3
        Assertions.assertThat(jobResult).isEqualTo("success")
        Assertions.assertThat(future.get()).isEqualTo("success")
    }

}
