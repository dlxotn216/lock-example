package io.taesu.lockexample.aop.lock

import io.taesu.lockexample.aop.SpElContextResolver
import io.taesu.lockexample.application.DistributedLockService
import io.taesu.lockexample.application.LockContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


/**
 * Created by itaesu on 2024/05/02.
 *
 * 분산 락을 취득하기 위한 Aspect
 *
 * @author Lee Tae Su
 */
@Aspect
@Component
class DistributedLockAspect(private val distributedLockService: DistributedLockService?) {
    @Around("@annotation(io.taesu.lockexample.aop.lock.AcquireDistributeLock)")
    fun aroundAcquireDistributeLock(joinPoint: ProceedingJoinPoint): Any? {
        distributedLockService ?: run {
            log.warn("DistributedLockService가 주입되지 않아 DistributedLock 없이 진행합니다.")
            return joinPoint.proceed()
        }

        val signature = joinPoint.signature as? MethodSignature ?: return null
        val method = signature.method

        val annotation = method.getAnnotation(AcquireDistributeLock::class.java)
        val spElContext = SpElContextResolver.resolve(joinPoint, annotation.key)
            ?: throw IllegalArgumentException("SpElContext를 resolve 할 수 없습니다.")

        val lockContext = LockContext(
            key = spElContext.key,
            waitTime = annotation.waitTime,
            lockTimeout = annotation.lockTimeout,
            timeUnit = annotation.timeUnit
        )
        return distributedLockService.tryJobWithLock(lockContext) {
            joinPoint.proceed()
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
