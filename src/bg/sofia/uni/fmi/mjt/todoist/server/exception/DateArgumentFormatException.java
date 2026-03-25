package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class DateArgumentFormatException extends TodoistLogicException {
    public DateArgumentFormatException(String message) {
        super(message);
    }
}
