package io.taesu.lockexample.domain

import jakarta.persistence.*

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
    val couponKey: Long = 0L,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "remained_stock", nullable = false)
    var remainedStock: Long,
): BaseEntity<Long>() {
    override fun getId(): Long = couponKey
}
