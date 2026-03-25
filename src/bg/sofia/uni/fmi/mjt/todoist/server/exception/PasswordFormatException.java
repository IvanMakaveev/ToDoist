package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class PasswordFormatException extends TodoistLogicException {
    public PasswordFormatException(String message) {
        super(message);
    }
}
