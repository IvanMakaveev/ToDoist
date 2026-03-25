package bg.sofia.uni.fmi.mjt.todoist.server.exception.base;

public class TodoistLogicException extends Exception {
    public TodoistLogicException(String message) {
        super(message);
    }

    public TodoistLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
