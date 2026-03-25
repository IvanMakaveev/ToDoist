package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class CollaborationExistsException extends TodoistLogicException {
    public CollaborationExistsException(String message) {
        super(message);
    }
}
