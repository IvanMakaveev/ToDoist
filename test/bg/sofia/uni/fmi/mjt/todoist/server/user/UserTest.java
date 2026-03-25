package bg.sofia.uni.fmi.mjt.todoist.server.user;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationUserExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskAlreadyFinishedException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class UserTest {
    private static final String testCollab = "test";
    private User testUser;
    private Task initialTask;

    @BeforeEach
    void setupUser() {
        testUser = new User("testName", "testPassword");
        initialTask = new Task("test", null, null, null);
        try {
            testUser.addTask(initialTask);
            testUser.addCollaboration(testCollab);
        } catch (TaskExistsException | CollaborationUserExistsException e) {
            fail("The setup method should not throw");
        }
    }

    @Test
    void testGetUserTasksWorksCorrectly() {
        assertIterableEquals(Set.of(initialTask), testUser.getUserTasks(),
            "The method should return the correct user tasks");
    }

    @Test
    void testGetCollaborationsWorksCorrectly() {
        assertIterableEquals(Set.of(testCollab), testUser.getCollaborations(),
            "The method should return the correct collaborations");
    }

    @Test
    void testArePasswordsEqualWorksCorrectly() {
        assertTrue(testUser.arePasswordsEqual("testPassword"),
            "The method should return true for equal passwords");

        assertFalse(testUser.arePasswordsEqual("test"),
            "The method should return false for different passwords");
    }

    @Test
    void testAddTaskThrowsWithNullTask() {
        assertThrows(IllegalArgumentException.class, () -> testUser.addTask(null),
            "The method should throw when attempting to add a null task");
    }

    @Test
    void testAddTaskThrowsWithRepeatedTask() {
        assertThrows(TaskExistsException.class, () -> testUser.addTask(new Task("test", null, null, null)),
            "The method should throw when attempting to add equal tasks");
    }

    @Test
    void testAddTaskWorksCorrectlyWithNewTaskSameDate() {
        Task testTask = new Task("testTask", null, null, null);

        try {
            testUser.addTask(testTask);
        } catch (TaskExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(testUser.getUserTasks().contains(testTask), "The task should be added successfully");
    }

    @Test
    void testAddTaskWorksCorrectlyWithSameTaskDifferentDate() {
        Task testTask = new Task("test", LocalDate.now(), null, null);

        try {
            testUser.addTask(testTask);
        } catch (TaskExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(testUser.getUserTasks().contains(testTask), "The task should be added successfully");
    }

    @Test
    void testUpdateTaskThrowsWithNullTask() {
        assertThrows(IllegalArgumentException.class, () -> testUser.updateTask(null),
            "The method should throw when attempting to update a null task");
    }

    @Test
    void testUpdateTaskThrowsWithMissingTask() {
        assertThrows(TaskNotFoundException.class,
            () -> testUser.updateTask(new Task("notFound", null, null, null)),
            "The method should throw when attempting to update missing tasks");
    }

    @Test
    void testUpdateTaskWorksCorrectly() {
        Task initialTaskOverwrite = new Task("test", null, LocalDate.now(), "test");

        try {
            testUser.updateTask(initialTaskOverwrite);
        } catch (TaskNotFoundException e) {
            fail("The method should not throw when called correctly");
        }

        Optional<Task> resultTask = testUser.getUserTasks().stream().findFirst();
        assertTrue(resultTask.isPresent(), "A task should be contained in the user's task set");
        assertEquals(initialTaskOverwrite, resultTask.get(),
            "The task should be equal to the task overwrite");
        assertEquals(initialTaskOverwrite.getDueDate(), resultTask.get().getDueDate(),
            "The task should be with updated due date");
        assertEquals(initialTaskOverwrite.getDescription(), resultTask.get().getDescription(),
            "The task should be with updated description");
    }

    @Test
    void testDeleteTaskThrowsWithNullTask() {
        assertThrows(IllegalArgumentException.class, () -> testUser.deleteTask(null),
            "The method should throw when attempting to delete a null task");
    }

    @Test
    void testDeleteTaskThrowsWithMissingTask() {
        assertThrows(TaskNotFoundException.class,
            () -> testUser.deleteTask(new Task("notFound", null, null, null)),
            "The method should throw when attempting to delete missing tasks");
    }

    @Test
    void testDeleteTaskWorksCorrectly() {
        try {
            testUser.deleteTask(initialTask);
        } catch (TaskNotFoundException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(testUser.getUserTasks().isEmpty(), "The method should delete the user's task correctly");
    }

    @Test
    void testGetTaskThrowsWithNullTask() {
        assertThrows(IllegalArgumentException.class, () -> testUser.getTask(null),
            "The method should throw when attempting to get a null task");
    }

    @Test
    void testGetTaskThrowsWithMissingTask() {
        assertThrows(TaskNotFoundException.class,
            () -> testUser.getTask(new Task("notFound", null, null, null)),
            "The method should throw when attempting to get a missing task");
    }

    @Test
    void testGetTaskWorksCorrectly() {
        String result = "";
        try {
            result = testUser.getTask(initialTask);
        } catch (TaskNotFoundException e) {
            fail("The method should not throw when called correctly");
        }

        assertEquals(initialTask.toString(), result, "The method should return the user's task string correctly");
    }

    @Test
    void testListTasksWorksCorrectlyNoMatches() {
        String result = testUser.listTasks(LocalDate.now(), true);

        assertEquals("", result, "The method should return an empty string");
    }

    @Test
    void testListTasksWorksCorrectlyWithMatches() {
        String result = testUser.listTasks(null, false);

        assertEquals(initialTask.toString(), result, "The method should return the initial task string");
    }

    @Test
    void testListTasksWorksCorrectlyWithMultipleMatches() {
        Task targetNewTask = new Task("test2", null, null, null);
        try {
            testUser.addTask(targetNewTask);
            testUser.addTask(new Task("test3", LocalDate.now(), null, null));
            Task complete = new Task("test3", null, null, null);
            complete.markAsFinished();
            testUser.addTask(complete);
        } catch (TaskExistsException | TaskAlreadyFinishedException e) {
            fail("Data setup should not throw");
        }

        String result = testUser.listTasks(null, false);

        assertTrue(result.contains(initialTask.toString()),
            "The method should return string containing the initial task");
        assertTrue(result.contains(targetNewTask.toString()), "The method should return string containing a new task");
    }

    @Test
    void testListDashboardWorksCorrectly() {
        String result = testUser.listDashboard();

        assertEquals(initialTask.toString(), result, "The method should return the initial task string");
    }

    @Test
    void testFinishTaskThrowsWithNullTask() {
        assertThrows(IllegalArgumentException.class, () -> testUser.finishTask(null),
            "The method should throw when attempting to finish a null task");
    }

    @Test
    void testFinishTaskThrowsWithMissingTask() {
        assertThrows(TaskNotFoundException.class,
            () -> testUser.finishTask(new Task("notFound", null, null, null)),
            "The method should throw when attempting to finish a missing task");
    }

    @Test
    void testFinishTaskWorksCorrectly() {
        try {
            testUser.finishTask(initialTask);
        } catch (TaskNotFoundException | TaskAlreadyFinishedException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(initialTask.isFinished(), "The method should finish the task correctly");
    }

    @Test
    void testFinishTaskThrowsMultipleFinishes() {
        try {
            testUser.finishTask(initialTask);
        } catch (TaskNotFoundException | TaskAlreadyFinishedException e) {
            fail("The method should not throw when called correctly");
        }

        assertThrows(TaskAlreadyFinishedException.class, () -> testUser.finishTask(initialTask),
            "The method should throw when attempting to finish a task twice");
    }

    @Test
    void testAddCollaborationThrowsWithNullCollaboration() {
        assertThrows(IllegalArgumentException.class, () -> testUser.addCollaboration(null),
            "The method should throw when attempting to add a null collaboration name");
    }

    @Test
    void testAddCollaborationThrowsWithRepeatedCollaboration() {
        assertThrows(CollaborationUserExistsException.class, () -> testUser.addCollaboration(testCollab),
            "The method should throw when attempting to add collaboration name twice");
    }

    @Test
    void testAddCollaborationWorksCorrectly() {
        try {
            testUser.addCollaboration("testCollab2");
        } catch (CollaborationUserExistsException e) {
            fail("The method should not throw when called correctly");
        }

        assertTrue(testUser.getCollaborations().contains("testCollab2"),
            "The method should add the collaboration given");
    }

    @Test
    void testListCollaborationsWorksCorrectly() {
        assertEquals(testCollab, testUser.listCollaborations(),
            "The method should return the initial collaboration string");
    }

    @Test
    void testListCollaborationsWorksCorrectlyWithMultipleCollaborations() {
        try {
            testUser.addCollaboration("testCollab");
        } catch (CollaborationUserExistsException e) {
            fail("Data setup should not throw");
        }

        assertTrue(testUser.listCollaborations().contains(testCollab),
            "The method should return string containing the initial collaboration");
        assertTrue(testUser.listCollaborations().contains("testCollab"),
            "The method should return string containing the new collaboration");
    }

    @Test
    void testRemoveCollaborationWorksCorrectly() {
        testUser.removeCollaboration(testCollab);

        assertTrue(testUser.listCollaborations().isEmpty(),
            "The method should remove the given collaboration correctly");
    }

    @Test
    void testGetIdWorksCorrectly() {
        assertEquals("testName", testUser.getId(), "The method should return the correct user ID");
    }
}
