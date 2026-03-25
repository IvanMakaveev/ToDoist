package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class UserInSessionException extends TodoistLogicException {
    public UserInSessionException(String message) {
        super(message);
    }
}
