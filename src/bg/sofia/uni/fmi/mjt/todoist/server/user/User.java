package bg.sofia.uni.fmi.mjt.todoist.server.user;

import bg.sofia.uni.fmi.mjt.todoist.server.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.database.identifiable.Identifiable;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskAlreadyFinishedException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationUserExistsException;

import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.stream.Collectors;

public class User implements Identifiable, Serializable {
    private final String username;
    private final String password;
    private final Set<Task> userTasks;
    private final Set<String> collaborations;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.userTasks = new HashSet<>();
        this.collaborations = new HashSet<>();
    }

    public boolean arePasswordsEqual(String password) {
        return this.password.equals(password);
    }

    public Set<Task> getUserTasks() {
        return this.userTasks;
    }

    public Set<String> getCollaborations() {
        return this.collaborations;
    }

    public void addTask(Task task) throws TaskExistsException {
        if (task == null) {
            throw new IllegalArgumentException("Cannot add a null task to user");
        }

        if (userTasks.contains(task)) {
            throw new TaskExistsException("Task " + task.getName() + " already exists for user " + this.username);
        }

        this.userTasks.add(task);
    }

    public void updateTask(Task task) throws TaskNotFoundException {
        if (task == null) {
            throw new IllegalArgumentException("Cannot update a null task for user");
        }

        if (!userTasks.contains(task)) {
            throw new TaskNotFoundException("Cannot update missing task "
                + task.getName() + " for user " + this.username);
        }

        this.userTasks.remove(task);
        this.userTasks.add(task);
    }

    public void deleteTask(Task task) throws TaskNotFoundException {
        if (task == null) {
            throw new IllegalArgumentException("Cannot delete a null task for user");
        }

        if (!userTasks.contains(task)) {
            throw new TaskNotFoundException("Cannot delete missing task "
                + task.getName() + " for user " + this.username);
        }

        this.userTasks.remove(task);
    }

    public String getTask(Task targetTask) throws TaskNotFoundException {
        if (targetTask == null) {
            throw new IllegalArgumentException("Cannot get the data of a null task for user");
        }

        return this.userTasks.stream()
            .filter(task -> task.equals(targetTask))
            .findFirst()
            .map(Task::toString)
            .orElseThrow(
                () -> new TaskNotFoundException("Cannot get data for missing task " +
                    targetTask.getName() + " for user " + this.username)
            );
    }

    public String listTasks(LocalDate targetDate, boolean isCompleted) {
        return this.userTasks.stream()
            .filter(task -> task.hasEqualDate(targetDate) && task.isFinished() == isCompleted)
            .map(Task::toString)
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public String listDashboard() {
        return this.listTasks(null, false);
    }

    public void finishTask(Task targetTask) throws TaskNotFoundException, TaskAlreadyFinishedException {
        if (targetTask == null) {
            throw new IllegalArgumentException("Cannot finish a null task for user");
        }

        if (!userTasks.contains(targetTask)) {
            throw new TaskNotFoundException("Cannot finish missing task " + targetTask.getName() +
                " for user " + this.username);
        }

        Task userTask = this.userTasks.stream()
            .filter(task -> task.equals(targetTask))
            .findFirst()
            .orElseThrow(
                () -> new TaskNotFoundException("Cannot finish missing task " +
                    targetTask.getName() + " for user " + this.username)
            );

        userTask.markAsFinished();
    }

    public void addCollaboration(String name) throws CollaborationUserExistsException {
        if (name == null) {
            throw new IllegalArgumentException("Cannot add a null collaboration for user");
        }

        if (this.collaborations.contains(name)) {
            throw new CollaborationUserExistsException("User " + this.username +
                " already added to collaboration " + name);
        }

        this.collaborations.add(name);
    }

    public String listCollaborations() {
        return this.collaborations.stream()
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public void removeCollaboration(String collaboration) {
        this.collaborations.remove(collaboration);
    }

    @Override
    public String getId() {
        return this.username;
    }
}
