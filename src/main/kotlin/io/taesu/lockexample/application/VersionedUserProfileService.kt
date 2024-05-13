package io.taesu.lockexample.application

import io.taesu.lockexample.domain.VersionedUserProfile
import io.taesu.lockexample.domain.VersionedUserProfileRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by itaesu on 2024/05/13.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Service
class VersionedUserProfileService(private val userProfileRepository: VersionedUserProfileRepository) {
    @Transactional
    fun update(userKey: Long, command: VersionedUserUpdateCommand) {
        userProfileRepository.getReferenceById(userKey).apply {
            log.info("[${Thread.currentThread().name}] Update user profile with $command")
            update(command.name, command.introduce, command.version)
        }
    }

    // Query requires transaction be in progress, but no transaction is known to be in progress
    fun justRead(userKey: Long, command: VersionedUserUpdateCommand): VersionedUserProfile {
        return userProfileRepository.findOptimisticLockByUserKey(userKey)
    }

    // Connection is read-only. Queries leading to data modification are not allowed
    @Transactional(readOnly = true)
    fun justReadOnly(userKey: Long, command: VersionedUserUpdateCommand): VersionedUserProfile {
        return userProfileRepository.findOptimisticLockByUserKey(userKey)
    }

    @Transactional
    fun justReadInTransaction(userKey: Long, command: VersionedUserUpdateCommand): VersionedUserProfile {
        return userProfileRepository.findOptimisticLockByUserKey(userKey)
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

data class VersionedUserUpdateCommand(
    val name: String,
    val introduce: String?,
    val version: Long,
)
