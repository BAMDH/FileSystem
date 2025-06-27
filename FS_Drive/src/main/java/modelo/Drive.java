package modelo;

/**
 *
 * @author Psicops
 */
public class Drive {
    private String username;
    private long maxSizeBytes;
    private long usedSpace;
    private Directory root;
    private Directory shared;
    private Directory current;

    public Drive(String username, long maxSizeBytes) {
        this.username = username;
        this.maxSizeBytes = maxSizeBytes;
        this.usedSpace = 0;
        this.root = new Directory("root", null);
        this.shared = new Directory("shared", null);
        this.current = root;
    }

    public void rebuildParents() {
        rebuildParentsRecursive(root, null);
        rebuildParentsRecursive(shared, null);
    }

    private void rebuildParentsRecursive(Directory dir, Directory parent) {
        dir.setParent(parent);
        if (dir.getSubdirectories() != null) {
            for (Directory child : dir.getSubdirectories()) {
                rebuildParentsRecursive(child, dir);
            }
        }
    }
    
    public boolean hasEnoughSpace(long size) {
        return usedSpace + size <= maxSizeBytes;
    }

    public void useSpace(long size) {
        usedSpace += size;
    }

    public void freeSpace(long size) {
        usedSpace -= size;
    }

    public void goToRoot() {
        current = root;
    }

    public void goToShared() {
        current = shared;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }

    public void setMaxSizeBytes(long maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public Directory getRoot() {
        return root;
    }

    public void setRoot(Directory root) {
        this.root = root;
    }

    public Directory getShared() {
        return shared;
    }

    public void setShared(Directory shared) {
        this.shared = shared;
    }

    public Directory getCurrent() {
        return current;
    }

    public void setCurrent(Directory current) {
        this.current = current;
    }
}
