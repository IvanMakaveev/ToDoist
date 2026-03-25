package bg.sofia.uni.fmi.mjt.todoist.server.collaboration;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationUserExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.NoUserInCollaborationException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.server.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class CollaborationTest {
    private static final String testOwner = "testName";
    private Collaboration testCollab;
    private User testUser;

    @BeforeEach
    void setupTestCollaboration() {
        testCollab = new Collaboration("test", testOwner);
        testUser = new User(testOwner, null);
    }

    @Test
    void testGetOwnerIdWorksCorrectly() {
        assertEquals(testOwner, testCollab.getOwnerId(),
            "The method should return correct owner");
    }

    @Test
    void testGetUsersWorksCorrectly() {
        assertIterableEquals(Set.of(testOwner), testCollab.getUsers(),
            "The method should return correct users");
    }

    @Test
    void testAddUserThrowsNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> testCollab.addUser(null),
            "The method should throw when adding a null username");
    }

    @Test
    void testAddUserThrowsExistingUsername() {
        assertThrows(CollaborationUserExistsException.class, () -> testCollab.addUser(testOwner),
            "The method should throw when adding an existing username");
    }

    @Test
    void testAddUserWorksCorrectly() {
        try {
            testCollab.addUser("testUser");
        } catch (CollaborationUserExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(testCollab.getUsers().contains("testUser"));
    }

    @Test
    void testAddTaskThrowsNullTask() {
        assertThrows(IllegalArgumentException.class,
            () -> testCollab.addTask(testUser, null),
            "The method should throw when adding a null task");
    }

    @Test
    void testAddTaskThrowsNullUser() {
        assertThrows(IllegalArgumentException.class,
            () -> testCollab.addTask(null, new Task("test", null, null, null)),
            "The method should throw when adding a null user");
    }

    @Test
    void testAddTaskThrowsMissingUser() {
        assertThrows(NoUserInCollaborationException.class,
            () -> testCollab.addTask(new User("missing", null), new Task("test", null, null, null)),
            "The method should throw when adding a task to user not in collaboration");
    }

    @Test
    void testAddTaskWorksCorrectly() {
        try {
            testCollab.addTask(testUser, new Task("test", null, null, null));
        } catch (NoUserInCollaborationException | TaskExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertEquals("Task: test, Date: ???, Due: ???, Description: ???, Finished: false Assignee:testName",
            testCollab.listTasks(), "The method should add a task to user correctly");
    }

    @Test
    void testAddTaskThrowsWhenAddingSameTaskToUser() {
        Task testTask = new Task("test", null, null, null);

        try {
            testCollab.addTask(testUser, testTask);
        } catch (NoUserInCollaborationException | TaskExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertThrows(TaskExistsException.class, () -> testCollab.addTask(testUser, testTask),
            "The method should throw when attempting to add same task twice to user");
    }

    @Test
    void testListTasksWorksCorrectly() {
        try {
            testCollab.addTask(testUser, new Task("test1", null, null, null));
            testCollab.addTask(testUser, new Task("test2", null, null, null));
        } catch (NoUserInCollaborationException | TaskExistsException e) {
            fail("Data setup should not throw");
        }

        String result = testCollab.listTasks();

        assertTrue(
            result.contains("Task: test1, Date: ???, Due: ???, Description: ???, Finished: false Assignee:testName"),
            "The method should list correct task");
        assertTrue(
            result.contains("Task: test2, Date: ???, Due: ???, Description: ???, Finished: false Assignee:testName"),
            "The method should list correct task");
    }

    @Test
    void testListUsersWorksCorrectly() {
        try {
            testCollab.addUser("testUser");
        } catch (CollaborationUserExistsException e) {
            fail("Data setup should not throw");
        }

        String result = testCollab.listUsers();

        assertTrue(result.contains(testOwner), "The method should list owner as user");
        assertTrue(result.contains("testUser"), "The method should list correct user");
    }

    @Test
    void testGetIdWorksCorrectly() {
        assertEquals("test", testCollab.getId(), "The method should return correct collab ID");
    }
}
