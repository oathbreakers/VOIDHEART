package oathbreakers.voidheart.core.folia.config

import kotlinx.serialization.Serializable

@Serializable
data class CoreConfiguration(val db: PostgresConfiguration = PostgresConfiguration())

@Serializable
data class PostgresConfiguration(
    val url: String = "jdbc:postgresql://localhost:5432/voidheart",
    val username: String = "postgres",
    val password: String = "postgres"
)
