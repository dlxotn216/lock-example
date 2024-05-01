package io.taesu.lockexample.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class BaseEntity<ID>: Persistable<ID> {
    @CreatedBy
    @Column(name = "created_by", nullable = false)
    var createdBy: Long? = null
        protected set

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedBy
    @Column(name = "modified_by", nullable = false)
    var modifiedBy: Long? = null
        protected set

    @LastModifiedDate
    @Column(name = "modified_at", nullable = false)
    var modifiedAt: LocalDateTime? = null
        protected set

    override fun isNew(): Boolean = this.createdAt == null
}

