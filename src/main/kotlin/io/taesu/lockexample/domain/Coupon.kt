package io.taesu.lockexample.domain

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Entity
@Table(name = "app_coupon")
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_key")
    val couponKey: Long = 0L,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "remained_stock", nullable = false)
    var remainedStock: Long,
): BaseEntity<Long>() {
    override fun getId(): Long = couponKey

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "coupon")
    private val _couponUsers: MutableSet<CouponUser> = mutableSetOf()
    val couponUsers get() = _couponUsers.toSet()

    fun issue(userKey: Long) {
        if (remainedStock < 1) {
            return
        }

        val couponUser = CouponUser(coupon = this, userKey = userKey)
        if (couponUsers.contains(couponUser)) {
            return
        }
        this._couponUsers += couponUser
        this.remainedStock--
    }
}

interface CouponRepository: JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "10000"))
    fun findReadLockByCouponKey(couponKey: Long): Coupon

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "10000"))
    fun findWriteLockByCouponKey(couponKey: Long): Coupon
}
