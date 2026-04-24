package org.example.project.model
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message  : String,
        val canRetry : Boolean = true
    ) : UiState<Nothing>()
}

fun <T> UiState<T>.isLoading()  = this is UiState.Loading
fun <T> UiState<T>.isSuccess()  = this is UiState.Success
fun <T> UiState<T>.isError()    = this is UiState.Error
fun <T> UiState<T>.getOrNull(): T? = (this as? UiState.Success)?.data
fun <T> UiState<T>.errorMessage(): String? = (this as? UiState.Error)?.message
