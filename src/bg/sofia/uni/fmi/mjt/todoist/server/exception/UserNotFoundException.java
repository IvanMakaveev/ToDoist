package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class UserNotFoundException extends TodoistLogicException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
