package io.taesu.lockexample.domain

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Entity
@Table(name = "app_coupon_user")
class CouponUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_user_key")
    val couponUserKey: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_key", nullable = false, updatable = false)
    val coupon: Coupon,

    @Column(name = "user_key", nullable = false, updatable = false)
    val userKey: Long,
): BaseEntity<Long>() {
    override fun getId(): Long = couponUserKey

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CouponUser) return false

        if (coupon != other.coupon) return false
        if (userKey != other.userKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coupon.hashCode()
        result = 31 * result + userKey.hashCode()
        return result
    }
}

interface CouponUserRepository: JpaRepository<CouponUser, Long> {
    fun countByCoupon_CouponKey(couponKey: Long): Long
}
