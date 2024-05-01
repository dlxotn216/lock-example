package io.taesu.lockexample.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer

/**
 * Created by taesu on 2024/05/01.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@Configuration
class RedisConfig {
    @Bean
    fun redissonClient(redisInfo: RedisProperties): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("redis://${redisInfo.host}:${redisInfo.port}")
            .setPassword(redisInfo.password)
            .setClientName(redisInfo.clientName)
        return Redisson.create(config)
    }

    @Bean
    fun redisConnectionFactory(redisInfo: RedisProperties): RedisConnectionFactory {
        return RedissonConnectionFactory(redissonClient(redisInfo))
    }

    @Bean
    fun redisTemplate(redisInfo: RedisProperties): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.keySerializer = RedisSerializer.string()
            this.valueSerializer = RedisSerializer.string()

            this.hashKeySerializer = RedisSerializer.string()
            this.hashValueSerializer = RedisSerializer.string()

            this.setDefaultSerializer(RedisSerializer.string())
            this.connectionFactory = redisConnectionFactory(redisInfo)
        }
    }

    @Bean
    fun redisStringTemplate(redisInfo: RedisProperties): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory(redisInfo))
    }
}
