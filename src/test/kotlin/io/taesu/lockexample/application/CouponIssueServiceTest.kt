package io.taesu.lockexample.application

import io.taesu.lockexample.domain.Coupon
import io.taesu.lockexample.domain.CouponRepository
import io.taesu.lockexample.domain.CouponUserRepository
import io.taesu.lockexample.helper.AbstractRdbTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@SpringBootTest
@ActiveProfiles("test")
class CouponIssueServiceTest: AbstractRdbTest() {
    @Autowired
    private lateinit var couponRepository: CouponRepository

    @Autowired
    private lateinit var couponUserRepository: CouponUserRepository

    @Autowired
    private lateinit var couponIssueService: CouponIssueService

    private val latch = CountDownLatch(100)
    private val executors = Executors.newFixedThreadPool(100)

    @Test
    fun `동시성을 제어하지 못한다`() {
        // given
        val couponKey = couponRepository.save(Coupon(name = "할인 이벤트", remainedStock = 100)).couponKey

        // when
        (1..10000L).forEach {
            executors.submit {
                couponIssueService.issue(it, couponKey)
                latch.countDown()
            }
        }

        latch.await()


        // then
        assertThat(couponUserRepository.countByCoupon_CouponKey(couponKey)).isNotEqualTo(100L)
        assertThat(couponRepository.getReferenceById(couponKey).remainedStock).isNotEqualTo(0L)
    }

    @Test
    fun `비관적 락(Shared lock)을 통해 동시성을 제어해야한다`() {
        // given
        val couponKey = couponRepository.save(Coupon(name = "할인 이벤트", remainedStock = 100L)).couponKey

        // when
        (1..10000L).forEach {
            executors.submit {
                couponIssueService.issueWithPessimisticReadLock(it, couponKey)
                latch.countDown()
            }
        }

        latch.await()


        // then
        assertThat(couponUserRepository.countByCoupon_CouponKey(couponKey)).isEqualTo(100)
        assertThat(couponRepository.getReferenceById(couponKey).remainedStock).isEqualTo(0L)
    }

    @Test
    fun `비관적 락(Exclusive) 을 통해 동시성을 제어해야한다`() {
        // given
        val couponKey = couponRepository.save(Coupon(name = "할인 이벤트", remainedStock = 100L)).couponKey

        // when
        (1..10000L).forEach {
            executors.submit {
                couponIssueService.issueWithPessimisticWriteLock(it, couponKey)
                latch.countDown()
            }
        }

        latch.await()


        // then
        assertThat(couponUserRepository.countByCoupon_CouponKey(couponKey)).isEqualTo(100L)
        assertThat(couponRepository.getReferenceById(couponKey).remainedStock).isEqualTo(0L)
    }
}
