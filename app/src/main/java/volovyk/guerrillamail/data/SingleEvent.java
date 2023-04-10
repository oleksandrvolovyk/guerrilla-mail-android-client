package volovyk.guerrillamail.data;

public class SingleEvent<T> {
    private boolean hasBeenHandled = false;
    private final T content;

    public SingleEvent(T content) {
        this.content = content;
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }
}
