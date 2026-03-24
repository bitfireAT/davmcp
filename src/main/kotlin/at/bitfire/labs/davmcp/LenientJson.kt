package at.bitfire.labs.davmcp

import kotlinx.serialization.json.Json

val LenientJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}