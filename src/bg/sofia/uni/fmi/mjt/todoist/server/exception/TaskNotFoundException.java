package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class TaskNotFoundException extends TodoistLogicException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
