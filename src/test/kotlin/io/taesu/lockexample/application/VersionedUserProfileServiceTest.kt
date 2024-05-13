package io.taesu.lockexample.application

import io.taesu.lockexample.domain.VersionedUserProfile
import io.taesu.lockexample.domain.VersionedUserProfileRepository
import io.taesu.lockexample.helper.AbstractRdbTest
import jakarta.persistence.TransactionRequiredException
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.exception.GenericJDBCException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.Executors

/**
 * Created by itaesu on 2024/05/13.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@SpringBootTest
@ActiveProfiles("test")
class VersionedUserProfileServiceTest: AbstractRdbTest() {
    @Autowired
    private lateinit var userProfileRepository: VersionedUserProfileRepository

    @Autowired
    private lateinit var userProfileService: VersionedUserProfileService

    @Test
    fun `변경 데이터가 소실되지 않도록 ObjectOptimisticLockingFailureException이 발생한다`() {
        // given
        val user = userProfileRepository.save(
            VersionedUserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        val executors = Executors.newFixedThreadPool(2)
        val futures = listOf(
            CompletableFuture.supplyAsync(
                {
                    userProfileService.update(
                        user.userKey,
                        VersionedUserUpdateCommand("change name", "hello", user.version)
                    )
                }, executors
            ),
            CompletableFuture.supplyAsync(
                {
                    userProfileService.update(
                        user.userKey,
                        VersionedUserUpdateCommand("taesu", "change introduce", user.version)
                    )
                }, executors
            )
        )

        // then
        val exception = assertThrows<CompletionException> {
            futures.forEach { it.join() }
        }
        assertThat(exception.cause).isInstanceOf(ObjectOptimisticLockingFailureException::class.java)
    }

    @Test
    fun `ObjectOptimisticLockingFailureException이 발생한다`() {
        // given
        val user = userProfileRepository.save(
            VersionedUserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        userProfileService.update(user.userKey, VersionedUserUpdateCommand("change name", "hello", user.version))
        Thread.sleep(500L)  // 앞선 트랜잭션 커밋 후 사용자가 다른 세션에서 업데이트 시도 하는 시나리오

        val exception = assertThrows<ObjectOptimisticLockingFailureException> {
            userProfileService.update(
                user.userKey,
                VersionedUserUpdateCommand("taesu", "change introduce", user.version)
            )
        }

        // then
        assertThat(exception).isInstanceOf(ObjectOptimisticLockingFailureException::class.java)
    }

    @Test
    fun `트랜잭션을 요구한다`() {
        // given
        val user = userProfileRepository.save(
            VersionedUserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        val exception = assertThrows<InvalidDataAccessApiUsageException> {
            userProfileService.justRead(user.userKey, VersionedUserUpdateCommand("change name", "hello", user.version))
        }

        // then
        assertThat(exception.cause).isInstanceOf(TransactionRequiredException::class.java)
    }

    @Test
    fun `읽기전용 트랜잭션은 실패한다`() {
        // given
        val user = userProfileRepository.save(
            VersionedUserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        val exception = assertThrows<JpaSystemException> {
            userProfileService.justReadOnly(user.userKey, VersionedUserUpdateCommand("change name", "hello", user.version))
        }


        // then
        assertThat(exception.cause).isInstanceOf(GenericJDBCException::class.java)
    }

    @Test
    fun `버전이 증가된다`() {
        // given
        val user = userProfileRepository.save(
            VersionedUserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        val result = userProfileService.justReadInTransaction(
            user.userKey,
            VersionedUserUpdateCommand("change name", "hello", user.version)
        )

        // then
        assertThat(result.version).isEqualTo(user.version + 1L)
    }
}

