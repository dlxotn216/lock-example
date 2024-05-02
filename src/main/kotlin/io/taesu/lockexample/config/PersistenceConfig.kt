package io.taesu.lockexample.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
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
class PersistenceConfig {
    @Bean
    fun auditorAware(): AuditorAware<Long> = MockAwareFromUserContext()

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    @ConfigurationProperties("distributedlock.datasource.hikari")
    fun distributedLockDataSource(): HikariDataSource {
        return DataSourceBuilder.create().type(HikariDataSource::class.java).build()
    }
}

/**
 * 프로토 타입 용 MockAwareFromUserContext
 */
class MockAwareFromUserContext(): AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> = Optional.ofNullable(1L)
}
