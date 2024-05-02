package io.taesu.lockexample.application

import java.util.concurrent.TimeUnit

/**
 * Created by taesu on 2024/05/02.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
interface DistributedLockService {
    fun tryJobWithLock(
        lockContext: LockContext,
        job: () -> Any?,
    ): Any?
}

data class LockContext(
    val key: String,
    val waitTime: Int,      // 락 대기 시간
    val lockTimeout: Int,   // 락 획득 후 타임아웃 시간
    val timeUnit: TimeUnit,
)
