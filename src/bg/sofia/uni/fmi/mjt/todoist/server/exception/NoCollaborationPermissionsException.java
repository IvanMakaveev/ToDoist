package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class NoCollaborationPermissionsException extends TodoistLogicException {
    public NoCollaborationPermissionsException(String message) {
        super(message);
    }
}
