package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class NoUserInCollaborationException extends TodoistLogicException {
    public NoUserInCollaborationException(String message) {
        super(message);
    }
}
