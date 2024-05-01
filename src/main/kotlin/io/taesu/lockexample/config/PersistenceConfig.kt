package io.taesu.lockexample.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.util.*

/**
 * Created by itaesu on 2024/05/01.
 *
 * JPA Auditing 설정
 *
 * @author Lee Tae Su
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(
    basePackages = ["io.taesu.lockexample"],
)
class PersistenceConfig() {
    @Bean
    fun auditorAware(): AuditorAware<Long> = MockAwareFromUserContext()
}

/**
 * 프로토 타입 용 MockAwareFromUserContext
 */
class MockAwareFromUserContext(): AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> = Optional.ofNullable(1L)
}
