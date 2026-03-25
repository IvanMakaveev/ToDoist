package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class TaskAlreadyFinishedException extends TodoistLogicException {
    public TaskAlreadyFinishedException(String message) {
        super(message);
    }
}
