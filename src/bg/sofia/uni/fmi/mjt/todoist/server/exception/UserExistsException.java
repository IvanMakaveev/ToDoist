package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class UserExistsException extends TodoistLogicException {
    public UserExistsException(String message) {
        super(message);
    }
}
