package bg.sofia.uni.fmi.mjt.todoist.server.command;

import bg.sofia.uni.fmi.mjt.todoist.server.application.TodoistApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {
    private final SelectionKey mockKey = mock();
    @Mock
    private TodoistApplication mockApp;
    @InjectMocks
    private CommandExecutor testExecutor;

    @Test
    void testExecuteUnknownCommandWorksCorrectly() {
        Command testCommand = new Command("test", new String[] {"arg1"});

        String result = testExecutor.execute(mockKey, testCommand);

        assertEquals("Unknown command", result, "Test command should result in unknown execution string");
    }

    @Test
    void testExecuteExitCommandWorksCorrectly() {
        Command exitCommand = new Command("exit", new String[] {"arg1"});

        String result = testExecutor.execute(mockKey, exitCommand);

        assertEquals("exit", result, "Exit command should result in exit execution string");
        verify(mockApp, times(1)).exit(eq(mockKey));
    }

    @Test
    void testExecuteRegisterCommandWorksCorrectly() {
        Command registerCommand = new Command("register", new String[] {"test", "test"});

        String result = testExecutor.execute(mockKey, registerCommand);

        assertEquals("User test registered successfully", result,
            "Register command should result in correct register execution string");
        try {
            verify(mockApp, times(1)).register(eq(mockKey), eq("test"), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteLoginCommandWorksCorrectly() {
        Command loginCommand = new Command("login", new String[] {"test", "test"});

        String result = testExecutor.execute(mockKey, loginCommand);

        assertEquals("User test logged in successfully", result,
            "Login command should result in correct login execution string");
        try {
            verify(mockApp, times(1)).login(eq(mockKey), eq("test"), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteRegisterCommandNotEnoughParameters() {
        Command registerCommand = new Command("register", new String[] {"test"});

        String result = testExecutor.execute(mockKey, registerCommand);

        assertEquals("ERROR: Invalid register command arguments count", result,
            "Register command should result in correct register error");

        try {
            verify(mockApp, times(0)).register(any(), any(), any());
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteLoginCommandNotEnoughParameters() {
        Command loginCommand = new Command("login", new String[] {"test"});

        String result = testExecutor.execute(mockKey, loginCommand);

        assertEquals("ERROR: Invalid login command arguments count", result,
            "Login command should result in correct login error");

        try {
            verify(mockApp, times(0)).login(any(), any(), any());
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteAddTaskCommandWorksCorrectly() {
        Command addTaskCommand = new Command("add-task",
            new String[] {"--name=test", "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, addTaskCommand);

        assertEquals("Task test added successfully", result,
            "Add task command should result in correct add task execution string");

        try {
            verify(mockApp, times(1))
                .addTask(eq(mockKey), eq("test"), eq(LocalDate.of(2023, 10, 11)), eq(null), eq(null));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteUpdateTaskCommandWorksCorrectly() {
        Command updateTaskCommand = new Command("update-task",
            new String[] {"--name=test", "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, updateTaskCommand);

        assertEquals("Task test updated successfully", result,
            "Update task command should result in correct update task execution string");

        try {
            verify(mockApp, times(1))
                .updateTask(eq(mockKey), eq("test"), eq(LocalDate.of(2023, 10, 11)), eq(null), eq(null));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteDeleteTaskCommandWorksCorrectly() {
        Command deleteTaskCommand = new Command("delete-task",
            new String[] {"--name=test", "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, deleteTaskCommand);

        assertEquals("Task test deleted successfully", result,
            "Delete task command should result in correct delete task execution string");

        try {
            verify(mockApp, times(1))
                .deleteTask(eq(mockKey), eq("test"), eq(LocalDate.of(2023, 10, 11)));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteGetTaskCommandWorksCorrectly() {
        try {
            when(mockApp.getTask(any(), any(), any())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command getTaskCommand = new Command("get-task",
            new String[] {"--name=test", "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, getTaskCommand);

        assertEquals("test response", result,
            "Get task command should result in correct task string");

        try {
            verify(mockApp, times(1))
                .getTask(eq(mockKey), eq("test"), eq(LocalDate.of(2023, 10, 11)));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteListTasksCommandWorksCorrectly() {
        try {
            when(mockApp.listTasks(any(), any(), anyBoolean())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command listTasksCommand = new Command("list-tasks",
            new String[] {"--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, listTasksCommand);

        assertEquals("test response", result,
            "List tasks command should result in correct task string");

        try {
            verify(mockApp, times(1))
                .listTasks(eq(mockKey), eq(LocalDate.of(2023, 10, 11)), eq(false));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteListDashboardCommandWorksCorrectly() {
        try {
            when(mockApp.listDashboard(any())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command listDashboardCommand = new Command("list-dashboard",
            new String[] {});

        String result = testExecutor.execute(mockKey, listDashboardCommand);

        assertEquals("test response", result,
            "List dashboard command should result in correct dashboard string");

        try {
            verify(mockApp, times(1))
                .listDashboard(eq(mockKey));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteFinishTaskCommandWorksCorrectly() {
        Command finishTaskCommand = new Command("finish-task",
            new String[] {"--name=test", "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, finishTaskCommand);

        assertEquals("Task test finished successfully", result,
            "Finish task command should result in correct finish task execution string");

        try {
            verify(mockApp, times(1))
                .finishTask(eq(mockKey), eq("test"), eq(LocalDate.of(2023, 10, 11)));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteAddCollaborationCommandWorksCorrectly() {
        Command addCollabCommand = new Command("add-collaboration",
            new String[] {"--name=test"});

        String result = testExecutor.execute(mockKey, addCollabCommand);

        assertEquals("Collaboration test added successfully", result,
            "Add collaboration command should result in correct execution string");

        try {
            verify(mockApp, times(1))
                .addCollaboration(eq(mockKey), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteDeleteCollaborationCommandWorksCorrectly() {
        Command deleteCollabCommand = new Command("delete-collaboration",
            new String[] {"--name=test"});

        String result = testExecutor.execute(mockKey, deleteCollabCommand);

        assertEquals("Collaboration test deleted successfully", result,
            "Delete collaboration command should result in correct execution string");

        try {
            verify(mockApp, times(1))
                .deleteCollaboration(eq(mockKey), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteListCollaborationsCommandWorksCorrectly() {
        try {
            when(mockApp.listCollaborations(any())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command listCollabsCommand = new Command("list-collaborations",
            new String[] {});

        String result = testExecutor.execute(mockKey, listCollabsCommand);

        assertEquals("test response", result,
            "List collaborations command should result in correct collaboration string");

        try {
            verify(mockApp, times(1))
                .listCollaborations(eq(mockKey));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteAddUserCommandWorksCorrectly() {
        Command addUserCommand = new Command("add-user",
            new String[] {"--collaboration=test", "--user=testUser"});

        String result = testExecutor.execute(mockKey, addUserCommand);

        assertEquals("User testUser added successfully to collaboration", result,
            "Add user command should result in correct result string");

        try {
            verify(mockApp, times(1))
                .addUser(eq(mockKey), eq("test"), eq("testUser"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteAssignTaskCommandWorksCorrectly() {
        Command assignTaskCommand = new Command("assign-task",
            new String[] {"--collaboration=test", "--user=testUser", "--task=test",
                "--date=11-10-2023"});

        String result = testExecutor.execute(mockKey, assignTaskCommand);

        assertEquals("Task test added successfully to collaboration", result,
            "Assign task command should result in correct result string");

        try {
            verify(mockApp, times(1))
                .addTask(eq(mockKey), eq("test"), eq("testUser"), eq("test"),
                    eq(LocalDate.of(2023, 10, 11)), eq(null), eq(null));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteListUsersCommandWorksCorrectly() {
        try {
            when(mockApp.listUsers(any(), any())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command listUsersCommand = new Command("list-users",
            new String[] {"--collaboration=test"});

        String result = testExecutor.execute(mockKey, listUsersCommand);

        assertEquals("test response", result,
            "List users command should result in correct result string");

        try {
            verify(mockApp, times(1))
                .listUsers(eq(mockKey), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteIncorrectDateFormat() {
        Command listUsersCommand = new Command("add-task",
            new String[] {"--name=test", "--date=10-25-2023"});

        String result = testExecutor.execute(mockKey, listUsersCommand);

        assertEquals("ERROR: Date arguments must be in format: dd-MM-yyyy", result,
            "Execution result should match incorrect date input format");

        try {
            verify(mockApp, times(0))
                .addTask(any(), any(), any(), any(), any());
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }

    @Test
    void testExecuteListTasksCommandWorksCorrectlyWithCollaboration() {
        try {
            when(mockApp.listCollaborationTasks(any(), any())).thenReturn("test response");
        } catch (Exception e) {
            fail("Mock method should not throw");
        }

        Command listTasksCommand = new Command("list-tasks",
            new String[] {"--collaboration=test"});

        String result = testExecutor.execute(mockKey, listTasksCommand);

        assertEquals("test response", result,
            "List tasks command should result in correct collaboration tasks string");

        try {
            verify(mockApp, times(1))
                .listCollaborationTasks(eq(mockKey), eq("test"));
        } catch (Exception e) {
            fail("Mock method should not throw");
        }
    }
}
