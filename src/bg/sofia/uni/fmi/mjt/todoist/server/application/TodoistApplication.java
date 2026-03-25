package bg.sofia.uni.fmi.mjt.todoist.server.application;

import bg.sofia.uni.fmi.mjt.todoist.server.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.server.database.CollaborationDatabase;
import bg.sofia.uni.fmi.mjt.todoist.server.database.UserDatabase;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationUserExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.InvalidCredentialsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.NoCollaborationPermissionsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.NoUserInCollaborationException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.PasswordFormatException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskAlreadyFinishedException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNameMissingException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.TaskExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserInSessionException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.todoist.server.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.server.user.PasswordManager;
import bg.sofia.uni.fmi.mjt.todoist.server.user.User;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.nio.channels.SelectionKey;

public class TodoistApplication {
    private final UserDatabase userDatabase;
    private final CollaborationDatabase collaborationDatabase;
    private final Map<SelectionKey, User> sessionUsers;

    public TodoistApplication(UserDatabase userDatabase, CollaborationDatabase collaborationDatabase) {
        this.userDatabase = userDatabase;
        this.collaborationDatabase = collaborationDatabase;
        this.sessionUsers = new HashMap<>();
    }

    public void register(SelectionKey key, String username, String password)
        throws UserExistsException, PasswordFormatException, UserInSessionException {
        if (this.sessionUsers.containsKey(key)) {
            throw new UserInSessionException("User is already logged in");
        }

        PasswordManager.validate(password);
        String hashedPassword = PasswordManager.hashPassword(password);

        User user = this.userDatabase.create(username, hashedPassword);
        this.sessionUsers.put(key, user);
        this.userDatabase.save();
    }

    public void login(SelectionKey key, String username, String password)
        throws InvalidCredentialsException, UserInSessionException {
        if (this.sessionUsers.containsKey(key)) {
            throw new UserInSessionException("User is already logged in");
        }

        String hashedPassword = PasswordManager.hashPassword(password);

        User user;
        try {
            user = this.userDatabase.get(username);
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException("The provided username cannot be found", e);
        }

        if (!user.arePasswordsEqual(hashedPassword)) {
            throw new InvalidCredentialsException("The provided password is invalid");
        }
        this.sessionUsers.put(key, user);
    }

    public void addTask(SelectionKey key, String name, LocalDate date, LocalDate dueDate, String description)
        throws UserNotLoggedInException, TaskExistsException, TaskNameMissingException {
        User user = getUserFromSession(key);

        if (name == null) {
            throw new TaskNameMissingException("Cannot create a task with no name");
        }

        user.addTask(new Task(name, date, dueDate, description));
        this.userDatabase.save();
    }

    public void updateTask(SelectionKey key, String name, LocalDate date, LocalDate dueDate, String description)
        throws UserNotLoggedInException, TaskNotFoundException, TaskNameMissingException {
        User user = getUserFromSession(key);

        if (name == null) {
            throw new TaskNameMissingException("Cannot update a task with no name");
        }

        user.updateTask(new Task(name, date, dueDate, description));
        this.userDatabase.save();
    }

    public void deleteTask(SelectionKey key, String name, LocalDate date)
        throws UserNotLoggedInException, TaskNotFoundException, TaskNameMissingException {
        User user = getUserFromSession(key);

        if (name == null) {
            throw new TaskNameMissingException("Cannot delete a task with no name");
        }

        user.deleteTask(new Task(name, date, null, null));
        this.userDatabase.save();
    }

    public String getTask(SelectionKey key, String name, LocalDate date)
        throws UserNotLoggedInException, TaskNotFoundException, TaskNameMissingException {
        User user = getUserFromSession(key);

        if (name == null) {
            throw new TaskNameMissingException("Cannot get a task with no name");
        }

        return user.getTask(new Task(name, date, null, null));
    }

    public String listTasks(SelectionKey key, LocalDate date, boolean isCompleted)
        throws UserNotLoggedInException {
        User user = getUserFromSession(key);
        String result = user.listTasks(date, isCompleted);

        if (result == null || result.isBlank()) {
            return "No tasks found matching these parameters";
        } else {
            return result;
        }
    }

    public String listDashboard(SelectionKey key)
        throws UserNotLoggedInException {
        User user = getUserFromSession(key);
        String result = user.listDashboard();

        if (result == null || result.isBlank()) {
            return "No incomplete tasks for today";
        } else {
            return result;
        }
    }

    public void finishTask(SelectionKey key, String name, LocalDate date)
        throws UserNotLoggedInException, TaskNotFoundException, TaskAlreadyFinishedException, TaskNameMissingException {
        User user = getUserFromSession(key);

        if (name == null) {
            throw new TaskNameMissingException("Cannot finish a task with no name");
        }

        user.finishTask(new Task(name, date, null, null));
        this.userDatabase.save();
    }

    public void addCollaboration(SelectionKey key, String name)
        throws CollaborationExistsException, UserNotLoggedInException, CollaborationUserExistsException {
        User user = getUserFromSession(key);
        this.collaborationDatabase.create(name, user.getId());
        user.addCollaboration(name);

        persistChanges();
    }

    public void deleteCollaboration(SelectionKey key, String name)
        throws UserNotLoggedInException, NoCollaborationPermissionsException, CollaborationNotFoundException {
        User user = getUserFromSession(key);
        Collaboration collab = this.collaborationDatabase.delete(name, user.getId());
        Set<String> users = collab.getUsers();
        this.userDatabase.removeCollaborations(users, name);

        persistChanges();
    }

    public String listCollaborations(SelectionKey key) throws UserNotLoggedInException {
        User user = getUserFromSession(key);
        String result = user.listCollaborations();

        if (result == null || result.isBlank()) {
            return "No collaborations found";
        } else {
            return result;
        }
    }

    public void addUser(SelectionKey key, String collaborationName, String username)
        throws UserNotLoggedInException, UserNotFoundException, CollaborationNotFoundException,
        CollaborationUserExistsException, NoCollaborationPermissionsException {
        User user = getUserFromSession(key);
        Collaboration collab = this.collaborationDatabase.get(collaborationName);

        if (!collab.getOwnerId().equals(user.getId())) {
            throw new NoCollaborationPermissionsException("Cannot add users to collaboration that you don't own");
        }

        User targetUser = this.userDatabase.get(username);
        collab.addUser(targetUser.getId());
        targetUser.addCollaboration(collaborationName);

        persistChanges();
    }

    public void addTask(SelectionKey key, String collaborationName, String username, String taskName, LocalDate date,
                        LocalDate dueDate, String description)
        throws UserNotLoggedInException, UserNotFoundException, CollaborationNotFoundException,
        NoUserInCollaborationException, NoCollaborationPermissionsException, TaskExistsException,
        TaskNameMissingException {
        User user = getUserFromSession(key);
        Collaboration collab = this.collaborationDatabase.get(collaborationName);

        if (!collab.getOwnerId().equals(user.getId())) {
            throw new NoCollaborationPermissionsException("Cannot add tasks to collaboration that you don't own");
        }

        if (taskName == null) {
            throw new TaskNameMissingException("Cannot add a task with no name to collaboration");
        }

        User targetUser = this.userDatabase.get(username);
        collab.addTask(targetUser, new Task(taskName, date, dueDate, description));

        persistChanges();
    }

    public String listCollaborationTasks(SelectionKey key, String collaborationName)
        throws UserNotLoggedInException, CollaborationNotFoundException, NoCollaborationPermissionsException {
        User user = getUserFromSession(key);
        Collaboration collab = this.collaborationDatabase.get(collaborationName);

        if (!collab.getUsers().contains(user.getId())) {
            throw new NoCollaborationPermissionsException("Cannot view tasks of collaboration that you aren't in");
        }

        String result = collab.listTasks();
        if (result == null || result.isBlank()) {
            return "No collaboration tasks found";
        } else {
            return result;
        }
    }

    public String listUsers(SelectionKey key, String collaborationName)
        throws UserNotLoggedInException, CollaborationNotFoundException, NoCollaborationPermissionsException {
        User user = getUserFromSession(key);
        Collaboration collab = this.collaborationDatabase.get(collaborationName);

        if (!collab.getUsers().contains(user.getId())) {
            throw new NoCollaborationPermissionsException("Cannot view users of collaboration that you aren't in");
        }

        return collab.listUsers();
    }

    public void exit(SelectionKey key) {
        persistChanges();
        this.sessionUsers.remove(key);
    }

    private User getUserFromSession(SelectionKey key) throws UserNotLoggedInException {
        if (!this.sessionUsers.containsKey(key)) {
            throw new UserNotLoggedInException("User not logged in");
        }
        return this.sessionUsers.get(key);
    }

    private void persistChanges() {
        this.userDatabase.save();
        this.collaborationDatabase.save();
    }
}
