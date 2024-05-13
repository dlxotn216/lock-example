package io.taesu.lockexample.application

import io.taesu.lockexample.domain.UserProfileRepository
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
class UserProfileService(private val userProfileRepository: UserProfileRepository) {
    @Transactional
    fun update(userKey: Long, command: UserUpdateCommand) {
        userProfileRepository.getReferenceById(userKey).apply {
            log.info("[${Thread.currentThread().name}] Update user profile with $command")
            update(command.name, command.introduce)
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

data class UserUpdateCommand(
    val name: String,
    val introduce: String?,
)
