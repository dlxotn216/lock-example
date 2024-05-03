package io.taesu.lockexample.application

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.sql.Connection
import javax.sql.DataSource


/**
 * Created by taesu on 2024/05/02.
 *
 * @author Lee Tae Su
 * @version lock-example
 * @since lock-example
 */
@ConditionalOnProperty(
    value = ["app.lock"],
    havingValue = "mysql",
    matchIfMissing = false
)
@Service
class MySqlDistributedLockService(
    @Qualifier("distributedLockDataSource") private val dataSource: DataSource
): DistributedLockService {
    override fun tryJobWithLock(
        lockContext: LockContext,
        job: () -> Any?,
    ): Any? {
        return dataSource.connection.use { connection ->
            ExecuteGetLockStatement(connection, lockContext).use {
                job()
            }
        }
    }
}

class ExecuteGetLockStatement(
    private val connection: Connection,
    private val lockContext: LockContext,
): AutoCloseable {
    init {
        val rs = connection.prepareStatement("SELECT GET_LOCK(?, ?)")
            .apply {
                setString(1, lockContext.key)
                setInt(2, lockContext.waitTime)
            }
            .executeQuery()
        if (!rs.next() || rs.getInt(1) != 1) {
            throw IllegalStateException("Lock 획득 실패")
        }
    }

    override fun close() {
        val rs = connection.prepareStatement("SELECT RELEASE_LOCK(?)")
            .apply {
                setString(1, lockContext.key)
            }
            .executeQuery()

        if (!rs.next() || rs.getInt(1) != 1) {
            log.warn("Lock release result was empty or not 1")
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
