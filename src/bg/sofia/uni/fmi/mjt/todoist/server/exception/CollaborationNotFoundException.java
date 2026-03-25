package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class CollaborationNotFoundException extends TodoistLogicException {
    public CollaborationNotFoundException(String message) {
        super(message);
    }
}
