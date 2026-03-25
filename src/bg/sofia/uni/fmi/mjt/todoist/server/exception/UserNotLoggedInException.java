package bg.sofia.uni.fmi.mjt.todoist.server.exception;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.base.TodoistLogicException;

public class UserNotLoggedInException extends TodoistLogicException {
    public UserNotLoggedInException(String message) {
        super(message);
    }
}
