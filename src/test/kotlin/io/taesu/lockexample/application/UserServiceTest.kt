package io.taesu.lockexample.application

import io.taesu.lockexample.domain.UserProfile
import io.taesu.lockexample.domain.UserProfileRepository
import io.taesu.lockexample.helper.AbstractRdbTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CompletableFuture
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
class UserServiceTest: AbstractRdbTest() {
    @Autowired
    private lateinit var userProfileRepository: UserProfileRepository

    @Autowired
    private lateinit var userProfileService: UserProfileService

    @Test
    fun `변경 데이터가 소실되는 Second Lost Update Problem이 발생한다`() {
        // given
        val user = userProfileRepository.save(
            UserProfile(
                email = "taesulee93@gmail.com",
                name = "lee",
                introduce = "hello"
            )
        )

        // when
        val executors = Executors.newFixedThreadPool(2)
        val futures = listOf(
            CompletableFuture.supplyAsync(
                { userProfileService.update(user.userKey, UserUpdateCommand("change name", "hello")) }, executors
            ),
            CompletableFuture.supplyAsync(
                { userProfileService.update(user.userKey, UserUpdateCommand("taesu", "change introduce")) }, executors
            )
        )
        futures.forEach { it.join() }

        // then
        val updated = userProfileRepository.getReferenceById(user.userKey)
        assertAll(
            "희박한 확률로 모든 변경 건이 커밋 된다",
            { assert(updated.name == "change name") },
            { assert(updated.introduce == "change introduce") }
        )
    }
}
