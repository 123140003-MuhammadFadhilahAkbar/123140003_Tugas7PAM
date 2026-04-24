package org.example.project.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status       : String           = "",
    val totalResults : Int              = 0,
    val articles     : List<Article>    = emptyList(),
    // NewsAPI mengembalikan "message" saat error
    val message      : String?          = null,
    val code         : String?          = null
)

@Serializable
data class Source(
    val id   : String? = null,
    val name : String  = "Unknown"
)

@Serializable
data class Article(
    val source      : Source  = Source(),
    val author      : String? = null,
    val title       : String  = "",
    val description : String? = null,
    val url         : String  = "",
    @SerialName("urlToImage")
    val urlToImage  : String? = null,
    @SerialName("publishedAt")
    val publishedAt : String  = "",
    val content     : String? = null
) {
    val id: String
        get() = url.hashCode().toString()

    val safeTitle: String
        get() = title.ifBlank { "Tanpa Judul" }
    val safeDescription: String
        get() = description ?: "Tidak ada deskripsi tersedia."

    val sourceName: String
        get() = source.name.ifBlank { "Unknown Source" }

    val formattedDate: String
        get() {
            return try {
                // Parse: 2024-01-15T10:30:00Z
                val parts = publishedAt.split("T")[0].split("-")
                val months = listOf(
                    "", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                    "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                )
                val month = parts[1].toIntOrNull() ?: 0
                "${parts[2]} ${months.getOrElse(month) { "?" }} ${parts[0]}"
            } catch (e: Exception) {
                publishedAt.take(10)
            }
        }

    val readTime: String
        get() {
            val words   = (content ?: description ?: "").split(" ").size
            val minutes = maxOf(1, words / 200)
            return "$minutes menit baca"
        }

    val preview: String
        get() = safeDescription.let {
            if (it.length > 150) it.take(150) + "..." else it
        }

    val category: String
        get() = when {
            sourceName.contains("tech", ignoreCase = true)     -> "Teknologi"
            sourceName.contains("sport", ignoreCase = true)    -> "Olahraga"
            sourceName.contains("business", ignoreCase = true) -> "Bisnis"
            sourceName.contains("health", ignoreCase = true)   -> "Kesehatan"
            sourceName.contains("science", ignoreCase = true)  -> "Sains"
            else -> "Umum"
        }

    val avatarColor: Long
        get() = when (category) {
            "Teknologi" -> 0xFF1565C0L
            "Olahraga"  -> 0xFF2E7D32L
            "Bisnis"    -> 0xFFE65100L
            "Kesehatan" -> 0xFFAD1457L
            "Sains"     -> 0xFF6A1B9AL
            else        -> 0xFF00897BL
        }
}
