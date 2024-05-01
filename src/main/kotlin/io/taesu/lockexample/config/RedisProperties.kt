package io.taesu.lockexample.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisProperties @ConstructorBinding constructor(
    val host: String,
    val port: Int,
    val password: String,
    val clientName: String,
)

