package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class TaskExistsException extends TodoistLogicException {
    public TaskExistsException(String message) {
        super(message);
    }
}
