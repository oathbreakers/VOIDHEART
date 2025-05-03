package oathbreakers.voidheart.core.folia.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import oathbreakers.voidheart.core.folia.config.CoreConfiguration
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

internal object ExposedHelper {
    private fun getDataSource(config: CoreConfiguration): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.db.url
            username = config.db.username
            password = config.db.password
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }

        return HikariDataSource(config)
    }

    fun setup(config: CoreConfiguration) {
        var datasource: HikariDataSource? = null
        val flyway: Flyway?
        try {
            datasource = getDataSource(config)

            flyway = Flyway.configure()
                .validateOnMigrate(false)
                .locations("classpath:migrations")
                .dataSource(datasource)
                .load()
            flyway.migrate()

            Database.connect(datasource)
        } catch (e: Exception) {
            datasource?.close()
            throw e
        }
    }
}