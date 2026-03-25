package bg.sofia.uni.fmi.mjt.todoist.server.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandTypeTest {
    @Test
    void testGetNameWorksCorrectly() {
        assertEquals("register", CommandType.REGISTER.getName(), "Command type name should match");
        assertEquals("login", CommandType.LOGIN.getName(), "Command type name should match");
        assertEquals("exit", CommandType.EXIT.getName(), "Command type name should match");
        assertEquals("add-task", CommandType.ADD_TASK.getName(), "Command type name should match");
        assertEquals("update-task", CommandType.UPDATE_TASK.getName(), "Command type name should match");
        assertEquals("delete-task", CommandType.DELETE_TASK.getName(), "Command type name should match");
        assertEquals("get-task", CommandType.GET_TASK.getName(), "Command type name should match");
        assertEquals("list-dashboard", CommandType.LIST_DASHBOARD.getName(), "Command type name should match");
        assertEquals("finish-task", CommandType.FINISH_TASK.getName(), "Command type name should match");
        assertEquals("add-collaboration", CommandType.ADD_COLLABORATION.getName(), "Command type name should match");
        assertEquals("delete-collaboration", CommandType.DELETE_COLLABORATION.getName(), "Command type name should match");
        assertEquals("list-collaborations", CommandType.LIST_COLLABORATIONS.getName(), "Command type name should match");
        assertEquals("add-user", CommandType.ADD_USER.getName(), "Command type name should match");
        assertEquals("assign-task", CommandType.ASSIGN_TASK.getName(), "Command type name should match");
        assertEquals("list-tasks", CommandType.LIST_TASKS.getName(), "Command type name should match");
        assertEquals("list-users", CommandType.LIST_USERS.getName(), "Command type name should match");
        assertEquals("unknown", CommandType.UNKNOWN.getName(), "Command type name should match");
    }
}
