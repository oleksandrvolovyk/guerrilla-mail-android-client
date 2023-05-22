package volovyk.guerrillamail.data

class SingleEvent<T>(private val content: T) {
    private var hasBeenHandled = false
    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }

    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
}