package bg.sofia.uni.fmi.mjt.todoist.server.database;

import bg.sofia.uni.fmi.mjt.todoist.server.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationExistsException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.server.exception.NoCollaborationPermissionsException;

public class CollaborationDatabase extends FileDatabase<Collaboration> {
    private static final String COLLABORATION_DATABASE_FILE_PATH
        = ".\\storage\\collaborations.txt";

    public CollaborationDatabase() {
        super(COLLABORATION_DATABASE_FILE_PATH);
    }

    public Collaboration create(String name, String ownerId) throws CollaborationExistsException {
        if (this.objects.containsKey(name)) {
            throw new CollaborationExistsException("Collaboration with name " + name + " already exists");
        }

        Collaboration collab = new Collaboration(name, ownerId);
        this.objects.put(name, collab);
        return collab;
    }

    public Collaboration delete(String name, String ownerId)
        throws CollaborationNotFoundException, NoCollaborationPermissionsException {
        Collaboration collab = this.get(name);

        if (!collab.getOwnerId().equals(ownerId)) {
            throw new NoCollaborationPermissionsException("The deletion of a collaboration can only be done by owner");
        }

        this.objects.remove(name);
        return collab;
    }

    public Collaboration get(String name) throws CollaborationNotFoundException {
        if (!this.objects.containsKey(name)) {
            throw new CollaborationNotFoundException("Collaboration with name " + name + " does not exist");
        }

        return this.objects.get(name);
    }
}
