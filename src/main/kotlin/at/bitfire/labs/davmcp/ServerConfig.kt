package at.bitfire.labs.davmcp

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import at.bitfire.labs.davmcp.db.Database
import java.util.*

data class ServerConfig(
    val calendarUrl: String,
    val username: String,
    val password: String
) {

    fun databaseDriver(): SqlDriver =
        JdbcSqliteDriver("jdbc:sqlite:data/users.db", Properties().apply {
            put("foreign_keys", "true")
        }, Database.Schema)


    companion object {

        fun fromEnvironment(): ServerConfig {
            fun requireEnv(name: String): String =
                System.getenv(name)?.takeIf { it.isNotBlank() }
                    ?: error("Required environment variable $name is not set")

            return ServerConfig(
                calendarUrl = requireEnv("CALDAV_URL"),
                username = requireEnv("CALDAV_USERNAME"),
                password = requireEnv("CALDAV_PASSWORD")
            )
        }
    }

}
