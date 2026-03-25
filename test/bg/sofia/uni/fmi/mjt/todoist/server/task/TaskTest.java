package bg.sofia.uni.fmi.mjt.todoist.server.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    private Task testTask;
    private LocalDate testDate;

    @BeforeEach
    void setupTestTask() {
        testDate = LocalDate.now();
        testTask = new Task("test", testDate, testDate.plusDays(10), "test2");
    }

    @Test
    void testTaskConstructionThrowsWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null, null, null, null),
            "The constructor should throw with null task name");
    }

    @Test
    void testGetNameWorksCorrectly() {
        assertEquals("test", testTask.getName(), "The method should return the correct name");
    }

    @Test
    void testGetDateWorksCorrectly() {
        assertEquals(testDate, testTask.getDate(), "The method should return the correct date");
    }

    @Test
    void testGetDueDateWorksCorrectly() {
        assertEquals(testDate.plusDays(10), testTask.getDueDate(),
            "The method should return the correct due date");
    }

    @Test
    void testGetDescriptionWorksCorrectly() {
        assertEquals("test2", testTask.getDescription(),
            "The method should return the correct description");
    }

    @Test
    void testHasEqualDateWorksCorrectlyEqualDates() {
        assertTrue(testTask.hasEqualDate(testDate), "The method should return true for equal dates");
    }

    @Test
    void testHasEqualDateWorksCorrectlyNullDates() {
        assertTrue(new Task("test", null, null, null).hasEqualDate(null),
            "The method should return true for equal null dates");
    }

    @Test
    void testHasEqualDateWorksCorrectlyDifferentDates() {
        assertFalse(testTask.hasEqualDate(testDate.plusDays(10)), "The method should return false for different dates");
    }

    @Test
    void testHasEqualDateWorksCorrectlyDifferentDatesWithOneNull() {
        assertFalse(testTask.hasEqualDate(null), "The method should return false for dates when one is null");
    }

    @Test
    void testEqualsForEqualTasks() {
        assertEquals(new Task("test", testDate, null, null), testTask,
            "The method should compare equal tasks correctly");
    }

    @Test
    void testEqualsForDifferentTasksByDate() {
        assertNotEquals(new Task("test", testDate.plusDays(10), null, null), testTask,
            "The method should compare different tasks by date correctly");
    }

    @Test
    void testEqualsForDifferentTasksByName() {
        assertNotEquals(new Task("test2", testDate, null, null), testTask,
            "The method should compare different tasks by name correctly");
    }

    @Test
    void testToStringWorksCorrectly() {
        assertEquals("Task: test, Date: 2024-02-07, Due: 2024-02-17, Description: test2, Finished: false",
            testTask.toString(),
            "The method should return correct string representation");
    }
}
