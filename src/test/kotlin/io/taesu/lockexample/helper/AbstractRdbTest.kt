package io.taesu.lockexample.helper

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@ActiveProfiles("rdb-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest
abstract class AbstractRdbTest {
    @Autowired
    private lateinit var cleanUp: CleanupDatabase

    @BeforeEach
    fun cleanup() {
        cleanUp.all()
    }
}
