package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class CollaborationUserExistsException extends TodoistLogicException {
    public CollaborationUserExistsException(String message) {
        super(message);
    }
}
