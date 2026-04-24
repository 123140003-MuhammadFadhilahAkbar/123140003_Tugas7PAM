package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.model.Article
import org.example.project.model.UiState
import org.example.project.repository.NewsRepository

class NewsViewModel(private val repository: NewsRepository) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // ── State list artikel ───────────────────────────────────────
    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

    // ── Refresh state ────────────────────────────────────────────
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ── Search ───────────────────────────────────────────────────
    var searchQuery by mutableStateOf("")
        private set

    // ── Kategori yang dipilih ────────────────────────────────────
    var selectedCategory by mutableStateOf("Top Headlines")
        private set

    // ── Bookmark ─────────────────────────────────────────────────
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    // Debounce job untuk search
    private var searchJob: Job? = null

    // Kategori yang tersedia
    val categories = listOf(
        "Top Headlines",
        "technology",
        "business",
        "health",
        "science",
        "sports",
        "entertainment"
    )

    init { loadTopHeadlines() }

    // ── Load top headlines ───────────────────────────────────────
    fun loadTopHeadlines() {
        scope.launch {
            _uiState.value = UiState.Loading
            selectedCategory = "Top Headlines"
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    _uiState.value = if (articles.isEmpty())
                        UiState.Error("Tidak ada berita tersedia saat ini.", canRetry = true)
                    else
                        UiState.Success(articles)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message  = error.message ?: "Gagal memuat berita",
                        canRetry = true
                    )
                }
        }
    }

    // ── Refresh ──────────────────────────────────────────────────
    fun refresh() {
        scope.launch {
            _isRefreshing.value = true
            val result = when {
                searchQuery.isNotBlank() -> repository.searchNews(searchQuery)
                selectedCategory != "Top Headlines" -> repository.getByCategory(selectedCategory)
                else -> repository.getTopHeadlines()
            }
            result
                .onSuccess { articles ->
                    _uiState.value = UiState.Success(articles)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message  = error.message ?: "Gagal memperbarui berita",
                        canRetry = true
                    )
                }
            _isRefreshing.value = false
        }
    }

    // ── Search dengan debounce 500ms ─────────────────────────────
    fun onSearchQueryChange(query: String) {
        searchQuery = query

        // Batalkan search sebelumnya
        searchJob?.cancel()

        if (query.isBlank()) {
            loadTopHeadlines()
            return
        }

        // Tunggu 500ms sebelum hit API (debounce)
        searchJob = scope.launch {
            delay(500)
            _uiState.value = UiState.Loading
            repository.searchNews(query)
                .onSuccess { articles ->
                    _uiState.value = if (articles.isEmpty())
                        UiState.Error("Tidak ada berita untuk \"$query\"", canRetry = false)
                    else
                        UiState.Success(articles)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message  = error.message ?: "Pencarian gagal",
                        canRetry = true
                    )
                }
        }
    }

    // ── Filter kategori ──────────────────────────────────────────
    fun onCategorySelected(category: String) {
        if (selectedCategory == category) return
        selectedCategory = category
        searchQuery = ""

        scope.launch {
            _uiState.value = UiState.Loading
            val result = if (category == "Top Headlines")
                repository.getTopHeadlines()
            else
                repository.getByCategory(category)

            result
                .onSuccess { articles ->
                    _uiState.value = UiState.Success(articles)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message  = error.message ?: "Gagal memuat kategori",
                        canRetry = true
                    )
                }
        }
    }

    // ── Bookmark ─────────────────────────────────────────────────
    fun toggleBookmark(articleId: String) {
        val current = _bookmarkedIds.value
        _bookmarkedIds.value = if (articleId in current)
            current - articleId
        else
            current + articleId
    }

    fun isBookmarked(articleId: String): Boolean =
        articleId in _bookmarkedIds.value
}
