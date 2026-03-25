package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class InvalidCommandFormatException extends TodoistLogicException {
    public InvalidCommandFormatException(String message) {
        super(message);
    }
}
