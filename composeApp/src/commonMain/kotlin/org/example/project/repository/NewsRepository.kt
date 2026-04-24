package org.example.project.repository

import io.ktor.client.*
import org.example.project.model.Article
import org.example.project.network.NewsApi

class NewsRepository(client: HttpClient) {

    private val api = NewsApi(client)

    suspend fun getTopHeadlines(): Result<List<Article>> {
        return try {
            val response = api.getTopHeadlines()

            if (response.status == "error") {
                return Result.failure(
                    Exception(mapApiError(response.code, response.message))
                )
            }

            val articles = response.articles.filter { article ->
                article.title.isNotBlank() &&
                article.title != "[Removed]" &&
                article.url.isNotBlank()
            }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }
    suspend fun searchNews(query: String): Result<List<Article>> {
        return try {
            val response = api.searchNews(query)

            if (response.status == "error") {
                return Result.failure(
                    Exception(mapApiError(response.code, response.message))
                )
            }

            val articles = response.articles.filter { article ->
                article.title.isNotBlank() &&
                article.title != "[Removed]" &&
                article.url.isNotBlank()
            }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }
    suspend fun getByCategory(category: String): Result<List<Article>> {
        return try {
            val response = api.getByCategory(category)

            if (response.status == "error") {
                return Result.failure(
                    Exception(mapApiError(response.code, response.message))
                )
            }

            val articles = response.articles.filter { article ->
                article.title.isNotBlank() &&
                article.title != "[Removed]"
            }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun mapApiError(code: String?, message: String?): String {
        return when (code) {
            "apiKeyInvalid"   -> "API key tidak valid. Periksa NewsApiConfig.kt dan masukkan key yang benar."
            "apiKeyMissing"   -> "API key tidak ditemukan. Pastikan sudah mengisi API key di NewsApiConfig.kt."
            "apiKeyExhausted" -> "Batas request API key harian habis (100/hari untuk free plan). Coba lagi besok."
            "rateLimited"     -> "Terlalu banyak request. Tunggu beberapa saat lalu coba lagi."
            "parameterInvalid"-> "Parameter tidak valid: ${message ?: "unknown"}"
            "sourcesTooMany"  -> "Terlalu banyak sumber dipilih."
            else              -> message ?: "Terjadi kesalahan dari server NewsAPI."
        }
    }

    private fun mapException(e: Exception): Exception {
        val message = when {
            e.message?.contains("timeout", ignoreCase = true) == true ->
                "Koneksi timeout. Periksa internet Anda dan coba lagi."
            e.message?.contains("unable to resolve", ignoreCase = true) == true ->
                "Tidak dapat terhubung ke server. Periksa koneksi internet."
            e.message?.contains("no address", ignoreCase = true) == true ->
                "Tidak ada koneksi internet. Aktifkan Wi-Fi atau data seluler."
            e.message?.contains("connection refused", ignoreCase = true) == true ->
                "Server tidak dapat dijangkau. Coba beberapa saat lagi."
            e.message?.contains("network", ignoreCase = true) == true ->
                "Gangguan jaringan. Periksa koneksi internet Anda."
            else ->
                "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
        }
        return Exception(message)
    }
}
