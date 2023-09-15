package volovyk.guerrillamail.util

sealed class State {
    data object Success : State()
    data object Loading : State()
    data class Failure(val error: Throwable) : State()
}