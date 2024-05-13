package io.taesu.lockexample.domain

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NaturalId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.orm.ObjectOptimisticLockingFailureException

/**
 * Created by itaesu on 2024/05/13.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Entity
@Table(name = "usr_user")
@DynamicUpdate
class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    val userKey: Long = 0L,

    @NaturalId
    @Column(name = "email", nullable = false, length = 255, unique = true)
    var email: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "introduce", nullable = true, length = 4000)
    var introduce: String?,
) {
    fun update(name: String, introduce: String?) {
        this.name = name
        this.introduce = introduce
    }
}

interface UserProfileRepository: JpaRepository<UserProfile, Long> {

}

@Entity
@Table(name = "usr_user_ver")
@DynamicUpdate
class VersionedUserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    val userKey: Long = 0L,

    @NaturalId
    @Column(name = "email", nullable = false, length = 255, unique = true)
    var email: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "introduce", nullable = true, length = 4000)
    var introduce: String?,

    version: Long = 0L,
): AbstractVersionedEntity(version) {
    override fun getId(): Long = userKey

    // fun update(name: String, introduce: String?, version: Long) {
    //     if (this.version != version) {
    //         throw ObjectOptimisticLockingFailureException(
    //             this::class.java,
    //             "[key:${this.userKey}][${this.version}:$version] Version mismatch"
    //         )
    //     }
    //     // this.version = version 설정 해줘도 where version에 바인딩 되는 것은 조회된 엔티티의 version 값이다.
    //     // 따라서 요청의 version mismatch 여부는 직접 확인해야 한다.
    //     this.name = name
    //     this.introduce = introduce
    // }

    fun update(name: String, introduce: String?, version: Long) {
        this.name = name
        this.introduce = introduce
        this.version = version
        // 혹은 Entity lifecycle에서 검증
    }
}

@MappedSuperclass
abstract class AbstractVersionedEntity(
    @Version
    var version: Long = 0L
) {
    abstract fun getId(): Long

    @Transient
    var previousVersion: Long = 0L

    @PostLoad
    fun afterLoad() {
        this.previousVersion = this.version
    }

    @PreUpdate
    fun beforeUpdate() {
        if(this.version != this.previousVersion) {
            throw ObjectOptimisticLockingFailureException(
                this::class.java,
                "[key:${this.getId()}][${this.version}:${this.previousVersion}] Version mismatch"
            )
        }
    }
}

interface VersionedUserProfileRepository: JpaRepository<VersionedUserProfile, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    fun findOptimisticLockByUserKey(userKey: Long): VersionedUserProfile

}
