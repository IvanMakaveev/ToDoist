package bg.sofia.uni.fmi.mjt.todoist.server.application;

import bg.sofia.uni.fmi.mjt.todoist.server.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.server.database.CollaborationDatabase;
import bg.sofia.uni.fmi.mjt.todoist.server.database.UserDatabase;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.InvalidCredentialsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.NoCollaborationPermissionsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.PasswordFormatException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNameMissingException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserInSessionException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.todoist.server.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.server.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoistApplicationTest {
    private final SelectionKey mockSelectionKey = mock();
    private final SelectionKey mockInitialKey = mock();
    private final User testUser = mock();
    private final LocalDate testDate = LocalDate.now();
    private final String initialUserName = "initialTest";
    private final Task testTask = new Task("test", testDate, testDate, "test");
    @Mock
    private UserDatabase mockUserDb;
    @Mock
    private CollaborationDatabase mockCollabDb;
    @InjectMocks
    private TodoistApplication testApp;

    @BeforeEach
    void setupData() {
        String passHash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        try {
            when(testUser.getId()).thenReturn(initialUserName);
            when(testUser.arePasswordsEqual(any())).thenReturn(false);
            when(testUser.arePasswordsEqual(eq(passHash))).thenReturn(true);
            when(mockUserDb.get(any())).thenReturn(testUser);
            testApp.login(mockInitialKey, initialUserName, "abc");
        } catch (Exception e) {
            fail("Setup should not throw");
        }
    }

    @Test
    void testRegisterWorksCorrectly() {
        try {
            when(mockUserDb.create(any(), any())).thenReturn(new User("test", "testPassword123"));
        } catch (UserExistsException e) {
            fail("Mock object should not throw");
        }

        try {
            testApp.register(mockSelectionKey, "test", "testPassword123");
        } catch (Exception e) {
            fail("Register method should not throw when called correctly");
        }

        try {
            verify(mockUserDb, times(1)).create(eq("test"), any());
        } catch (UserExistsException e) {
            fail("Mock object should not throw");
        }
        verify(mockUserDb, times(1)).save();
    }

    @Test
    void testRegisterThrowsIncorrectPassword() {
        assertThrows(PasswordFormatException.class,
            () -> testApp.register(mockSelectionKey, "test", "test"),
            "Registration method should throw when password format is incorrect");
    }

    @Test
    void testRegisterThrowsUserAddedTwice() {
        try {
            when(mockUserDb.create(any(), any())).thenReturn(new User("test", "testPassword123"));
        } catch (UserExistsException e) {
            fail("Mock object should not throw");
        }

        try {
            testApp.register(mockSelectionKey, "test", "testPassword123");
        } catch (Exception e) {
            fail("Register method should not throw when called correctly");
        }

        assertThrows(UserInSessionException.class,
            () -> testApp.register(mockSelectionKey, "test", "testPassword123"),
            "Register method should throw when user is already in session");
    }

    @Test
    void testLoginWorksCorrectly() {
        String passHash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        try {
            when(mockUserDb.get(any())).thenReturn(new User("test", passHash));
        } catch (UserNotFoundException e) {
            fail("Mock object should not throw");
        }

        try {
            testApp.login(mockSelectionKey, "test", "abc");
        } catch (Exception e) {
            fail("Login method should not throw when called correctly");
        }

        try {
            verify(mockUserDb, times(1)).get(eq("test"));
        } catch (UserNotFoundException e) {
            fail("Mock object should not throw");
        }
    }

    @Test
    void testLoginThrowsUserAddedTwice() {
        String passHash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        try {
            when(mockUserDb.get(any())).thenReturn(new User("test", passHash));
        } catch (UserNotFoundException e) {
            fail("Mock object should not throw");
        }

        try {
            testApp.login(mockSelectionKey, "test", "abc");
        } catch (Exception e) {
            fail("Login method should not throw when called correctly");
        }

        assertThrows(UserInSessionException.class,
            () -> testApp.login(mockSelectionKey, "test", "abc"),
            "Login method should throw when user is already in session");
    }

    @Test
    void testLoginThrowsWrongPassword() {
        String passHash = "wrong";
        try {
            when(mockUserDb.get(any())).thenReturn(new User("test", passHash));
        } catch (UserNotFoundException e) {
            fail("Mock object should not throw");
        }

        assertThrows(InvalidCredentialsException.class,
            () -> testApp.login(mockSelectionKey, "test", "abc"),
            "Login method should throw when user password is wrong");
    }

    @Test
    void testLoginThrowsWrongUsername() {
        try {
            when(mockUserDb.get(any())).thenThrow(UserNotFoundException.class);
        } catch (UserNotFoundException e) {
            fail("Mock object should not throw");
        }

        assertThrows(InvalidCredentialsException.class,
            () -> testApp.login(mockSelectionKey, "test", "abc"),
            "Login method should throw when username is wrong");
    }

    @Test
    void testAddTaskWorksCorrectly() {
        try {
            testApp.addTask(mockInitialKey, "test", testDate, testDate, "test");
        } catch (Exception e) {
            fail("Add task method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).addTask(eq(testTask));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
        verify(mockUserDb, times(1)).save();
    }

    @Test
    void testAddTaskThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.addTask(mockSelectionKey, "test", testDate, testDate, "test"),
            "Add task method should throw when user is not in session");
    }

    @Test
    void testAddTaskThrowsNoTaskName() {
        assertThrows(TaskNameMissingException.class,
            () -> testApp.addTask(mockInitialKey, null, testDate, testDate, "test"),
            "Add task method should throw when task name is null");
    }

    @Test
    void testUpdateTaskWorksCorrectly() {
        try {
            testApp.updateTask(mockInitialKey, "test", testDate, testDate, "test");
        } catch (Exception e) {
            fail("Update task method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).updateTask(eq(testTask));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
        verify(mockUserDb, times(1)).save();
    }

    @Test
    void testUpdateTaskThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.updateTask(mockSelectionKey, "test", testDate, testDate, "test"),
            "Update task method should throw when user is not in session");
    }

    @Test
    void testUpdateTaskThrowsNoTaskName() {
        assertThrows(TaskNameMissingException.class,
            () -> testApp.updateTask(mockInitialKey, null, testDate, testDate, "test"),
            "Update task method should throw when task name is null");
    }

    @Test
    void testDeleteTaskThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.deleteTask(mockSelectionKey, "test", testDate),
            "Delete task method should throw when user is not in session");
    }

    @Test
    void testDeleteTaskThrowsNoTaskName() {
        assertThrows(TaskNameMissingException.class,
            () -> testApp.deleteTask(mockInitialKey, null, testDate),
            "Delete task method should throw when task name is null");
    }

    @Test
    void testDeleteTaskWorksCorrectly() {
        try {
            testApp.deleteTask(mockInitialKey, "test", testDate);
        } catch (Exception e) {
            fail("Delete task method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).deleteTask(eq(testTask));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
        verify(mockUserDb, times(1)).save();
    }

    @Test
    void testGetTaskWorksCorrectly() {
        try {
            when(testUser.getTask(any())).thenReturn("test");
        } catch (TaskNotFoundException e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("test", testApp.getTask(mockInitialKey, "test", testDate),
                "Method should return correct result");
        } catch (Exception e) {
            fail("Get task should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).getTask(eq(testTask));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testGetTaskThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.getTask(mockSelectionKey, "test", testDate),
            "Get task method should throw when user is not in session");
    }

    @Test
    void testGetTaskThrowsNoTaskName() {
        assertThrows(TaskNameMissingException.class,
            () -> testApp.getTask(mockInitialKey, null, testDate),
            "Get task method should throw when task name is null");
    }

    @Test
    void testListTasksWorksCorrectly() {
        try {
            when(testUser.listTasks(any(), anyBoolean())).thenReturn("test");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("test", testApp.listTasks(mockInitialKey, testDate, true),
                "Method should return correct result");
        } catch (Exception e) {
            fail("List tasks should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listTasks(eq(testDate), eq(true));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListTasksWorksCorrectlyEmptyResult() {
        try {
            when(testUser.listTasks(any(), anyBoolean())).thenReturn("");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("No tasks found matching these parameters", testApp.listTasks(mockInitialKey, testDate, true),
                "Method should return correct result");
        } catch (Exception e) {
            fail("List tasks should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listTasks(eq(testDate), eq(true));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListTasksThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.listTasks(mockSelectionKey, testDate, true),
            "List tasks method should throw when user is not in session");
    }

    @Test
    void testListDashboardThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.listDashboard(mockSelectionKey),
            "List dashboard method should throw when user is not in session");
    }

    @Test
    void testListDashboardWorksCorrectly() {
        try {
            when(testUser.listDashboard()).thenReturn("test");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("test", testApp.listDashboard(mockInitialKey),
                "Method should return correct result");
        } catch (Exception e) {
            fail("List dashboard should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listDashboard();
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListDashboardWorksCorrectlyEmptyResult() {
        try {
            when(testUser.listDashboard()).thenReturn("");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("No incomplete tasks for today", testApp.listDashboard(mockInitialKey),
                "Method should return correct result");
        } catch (Exception e) {
            fail("List dashboard should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listDashboard();
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testFinishTaskWorksCorrectly() {
        try {
            testApp.finishTask(mockInitialKey, "test", testDate);
        } catch (Exception e) {
            fail("Finish task method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).finishTask(eq(testTask));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
        verify(mockUserDb, times(1)).save();
    }

    @Test
    void testFinishTaskThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.finishTask(mockSelectionKey, "test", testDate),
            "Finish task method should throw when user is not in session");
    }

    @Test
    void testFinishTaskThrowsNoTaskName() {
        assertThrows(TaskNameMissingException.class,
            () -> testApp.finishTask(mockInitialKey, null, testDate),
            "Finish task method should throw when task name is null");
    }

    @Test
    void testAddCollaborationWorksCorrectly() {
        try {
            testApp.addCollaboration(mockInitialKey, "test");
        } catch (Exception e) {
            fail("Add collaboration method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).addCollaboration(eq("test"));
            verify(mockCollabDb, times(1)).create(eq("test"), eq(initialUserName));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        verify(mockUserDb, times(1)).save();
        verify(mockCollabDb, times(1)).save();
    }

    @Test
    void testAddCollaborationThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.addCollaboration(mockSelectionKey, "test"),
            "Add collaboration method should throw when user is not in session");
    }

    @Test
    void testDeleteCollaborationWorksCorrectly() {
        try {
            when(mockCollabDb.delete(any(), any())).thenReturn(new Collaboration("test", initialUserName));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            testApp.deleteCollaboration(mockInitialKey, "test");
        } catch (Exception e) {
            fail("Delete collaboration method should not throw when called correctly");
        }

        try {
            verify(mockUserDb, times(1)).removeCollaborations(any(), eq("test"));
            verify(mockCollabDb, times(1)).delete(eq("test"), eq(initialUserName));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        verify(mockUserDb, times(1)).save();
        verify(mockCollabDb, times(1)).save();
    }

    @Test
    void testDeleteCollaborationThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.deleteCollaboration(mockSelectionKey, "test"),
            "Delete collaboration method should throw when user is not in session");
    }

    @Test
    void testListCollaborationsWorksCorrectly() {
        try {
            when(testUser.listCollaborations()).thenReturn("test");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("test", testApp.listCollaborations(mockInitialKey),
                "List collaborations method should return correct string result");
        } catch (Exception e) {
            fail("List collaborations method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listCollaborations();
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListCollaborationsWorksCorrectlyNoCollaborations() {
        try {
            when(testUser.listCollaborations()).thenReturn("");
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            assertEquals("No collaborations found", testApp.listCollaborations(mockInitialKey),
                "List collaborations method should return correct string result for no collaborations");
        } catch (Exception e) {
            fail("List collaborations method should not throw when called correctly");
        }

        try {
            verify(testUser, times(1)).listCollaborations();
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListCollaborationsThrowsMissingUser() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.listCollaborations(mockSelectionKey),
            "lIST collaborationS method should throw when user is not in session");
    }

    @Test
    void testAddUserWorksCorrectly() {
        User newUser = new User("testUser", "test");
        Collaboration newCollab = new Collaboration("test", initialUserName);

        try {
            when(mockUserDb.get(any())).thenReturn(newUser);
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            testApp.addUser(mockInitialKey, "test", "test");
        } catch (Exception e) {
            fail("Add user method should not throw when called correctly");
        }

        assertTrue(newUser.getCollaborations().contains("test"),
            "The collaboration should be added to the new user");
        assertTrue(newCollab.getUsers().contains("testUser"),
            "The user should be added to the collaboration");

        try {
            verify(mockUserDb, times(1)).get(eq("test"));
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        verify(mockUserDb, times(1)).save();
        verify(mockCollabDb, times(1)).save();
    }

    @Test
    void testAddUserThrowsInsufficientPermissions() {
        try {
            when(mockCollabDb.get(any())).thenReturn(new Collaboration("test", "wrong"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        assertThrows(NoCollaborationPermissionsException.class,
            () -> testApp.addUser(mockInitialKey, "test", "test"),
            "Add user method should throw when called without owner permissions");
    }

    @Test
    void testAddUserThrowsUserNotLogged() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.addUser(mockSelectionKey, "test", "test"),
            "Add user method should throw when called without being logged in");
    }

    @Test
    void testAddTaskToCollaborationWorksCorrectly() {
        Collaboration newCollab = new Collaboration("test", initialUserName);

        try {
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            testApp.addTask(mockInitialKey, "test", initialUserName,
                "test", testDate, testDate, "test");
        } catch (Exception e) {
            fail("Add user method should not throw when called correctly");
        }

        assertEquals(
            "Task: test, Date: 2024-02-07, Due: 2024-02-07, Description: test, Finished: false Assignee:initialTest",
            newCollab.listTasks(),
            "The task should be added correctly to the collaboration");

        try {
            verify(mockUserDb, times(2)).get(eq(initialUserName));
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        verify(mockUserDb, times(1)).save();
        verify(mockCollabDb, times(1)).save();
    }

    @Test
    void testAddTaskToCollaborationThrowsInsufficientPermissions() {
        try {
            when(mockCollabDb.get(any())).thenReturn(new Collaboration("test", "wrong"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        assertThrows(NoCollaborationPermissionsException.class,
            () -> testApp.addTask(mockInitialKey, "test", "test", "test",
                null, null, null),
            "Add task method should throw when called without owner permissions");
    }

    @Test
    void testAddTaskToCollaborationThrowsUserNotLogged() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.addTask(mockSelectionKey, "test", "test", "test",
                null, null, null),
            "Add task method should throw when called without being logged in");
    }

    @Test
    void testAddTaskToCollaborationThrowsMissingTask() {
        try {
            when(mockCollabDb.get(any())).thenReturn(new Collaboration("test", initialUserName));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        assertThrows(TaskNameMissingException.class,
            () -> testApp.addTask(mockInitialKey, "test", "test", null,
                null, null, null),
            "Add task method should throw when called with null task name");
    }

    @Test
    void testListCollaborationTasksWorksCorrectly() {
        Collaboration newCollab = new Collaboration("test", initialUserName);
        try {
            newCollab.addTask(testUser, testTask);
        } catch (Exception e) {
            fail("Data setup should not fail");
        }

        try {
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            String result = testApp.listCollaborationTasks(mockInitialKey, "test");
            assertEquals(
                "Task: test, Date: 2024-02-07, Due: 2024-02-07, Description: test, Finished: false Assignee:initialTest",
                result,
                "List collaboration tasks method should return correct string");
        } catch (Exception e) {
            fail("List collaboration tasks method should not throw when called correctly");
        }

        try {
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListCollaborationTasksWorksCorrectlyNoTasks() {
        Collaboration newCollab = new Collaboration("test", initialUserName);

        try {
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            String result = testApp.listCollaborationTasks(mockInitialKey, "test");
            assertEquals("No collaboration tasks found",
                result,
                "List collaboration tasks method should return correct string when no tasks");
        } catch (Exception e) {
            fail("List collaboration tasks method should not throw when called correctly");
        }

        try {
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListCollaborationTasksThrowsInsufficientPermissions() {
        try {
            when(mockCollabDb.get(any())).thenReturn(new Collaboration("test", "wrong"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        assertThrows(NoCollaborationPermissionsException.class,
            () -> testApp.listCollaborationTasks(mockInitialKey, "test"),
            "List collaboration tasks should throw when called without owner permissions");
    }

    @Test
    void testListCollaborationTasksThrowsUserNotLogged() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.listCollaborationTasks(mockSelectionKey, "test"),
            "List collaboration tasks method should throw when called without being logged in");
    }

    @Test
    void testListUsersWorksCorrectly() {
        Collaboration newCollab = new Collaboration("test", initialUserName);
        try {
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            String result = testApp.listUsers(mockInitialKey, "test");
            assertEquals(initialUserName, result, "List users method should return correct string");
        } catch (Exception e) {
            fail("List collaboration tasks method should not throw when called correctly");
        }

        try {
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListUsersWorksCorrectlyMultipleUsers() {
        Collaboration newCollab = new Collaboration("test", initialUserName);
        try {
            newCollab.addUser("testUser");
        } catch (Exception e) {
            fail("Data setup should not fail");
        }

        try {
            when(mockCollabDb.get(any())).thenReturn(newCollab);
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        try {
            String result = testApp.listUsers(mockInitialKey, "test");
            assertTrue(result.contains(initialUserName), "List users method should return string with owner user");
            assertTrue(result.contains("testUser"), "List users method should return string with new user");
        } catch (Exception e) {
            fail("List collaboration tasks method should not throw when called correctly");
        }

        try {
            verify(mockCollabDb, times(1)).get(eq("test"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }
    }

    @Test
    void testListUsersThrowsInsufficientPermissions() {
        try {
            when(mockCollabDb.get(any())).thenReturn(new Collaboration("test", "wrong"));
        } catch (Exception e) {
            fail("Mock method should not fail");
        }

        assertThrows(NoCollaborationPermissionsException.class,
            () -> testApp.listUsers(mockInitialKey, "test"),
            "List users should throw when called without owner permissions");
    }

    @Test
    void testListUsersThrowsUserNotLogged() {
        assertThrows(UserNotLoggedInException.class,
            () -> testApp.listUsers(mockSelectionKey, "test"),
            "List users method should throw when called without being logged in");
    }

    @Test
    void testExitWorksCorrectly() {
        testApp.exit(mockInitialKey);
        verify(mockUserDb, times(1)).save();
        verify(mockCollabDb, times(1)).save();
    }
}
