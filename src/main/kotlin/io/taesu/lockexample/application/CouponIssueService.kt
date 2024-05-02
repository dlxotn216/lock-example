package io.taesu.lockexample.application

import io.taesu.lockexample.aop.lock.AcquireDistributeLock
import io.taesu.lockexample.domain.CouponRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Service
class CouponIssueService(
    private val couponRepository: CouponRepository
) {
    @Transactional
    fun issue(userKey: Long, couponKey: Long) {
        val coupon = couponRepository.getReferenceById(couponKey)
        coupon.issue(userKey)
        couponRepository.save(coupon)
        log.info("${Thread.currentThread().name}:[$userKey][$couponKey]")
    }


    @Transactional
    fun issueWithPessimisticReadLock(userKey: Long, couponKey: Long) {
        val coupon = couponRepository.findReadLockByCouponKey(couponKey)
        coupon.issue(userKey)
        couponRepository.save(coupon)
        log.info("${Thread.currentThread().name}:[$userKey][$couponKey]")
    }

    @Transactional
    fun issueWithPessimisticWriteLock(userKey: Long, couponKey: Long) {
        val coupon = couponRepository.findWriteLockByCouponKey(couponKey)
        coupon.issue(userKey)
        couponRepository.save(coupon)
        log.info("${Thread.currentThread().name}:[$userKey][$couponKey]")
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }
}

@Service
class CouponIssueLockedService(
    private val couponIssueService: CouponIssueService
) {
    @AcquireDistributeLock(key = "'lock:coupon:' + #couponKey")
    fun issueWithDistributedLock(userKey: Long, couponKey: Long) {
        couponIssueService.issue(userKey, couponKey)
    }
}
