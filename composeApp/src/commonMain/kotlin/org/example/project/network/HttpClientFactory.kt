package org.example.project.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(): HttpClient {
        return HttpClient {

            // ── JSON Serialization ───────────────────────────────
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint       = true
                    isLenient         = true
                    ignoreUnknownKeys = true  // Abaikan field tidak dikenal
                    coerceInputValues = true  // null → default value
                })
            }

            // ── Logging ──────────────────────────────────────────
            install(Logging) {
                level  = LogLevel.HEADERS
                logger = Logger.DEFAULT
            }

            // ── Timeout ──────────────────────────────────────────
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis  = 10_000
            }

            // ── Default Request: tambah API key ke semua request ─
            defaultRequest {
                headers.append("X-Api-Key", NewsApiConfig.API_KEY)
                contentType(ContentType.Application.Json)
            }
        }
    }
}
