package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class TaskNameMissingException extends TodoistLogicException {
    public TaskNameMissingException(String message) {
        super(message);
    }
}
