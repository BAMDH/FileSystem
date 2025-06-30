package modelo;

import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author Psicops
 */
public class Drive {
    private String username;
    private long maxSizeBytes;
    private long usedSpace;
    private Directory root;
    private Directory current;

    public Drive(String username, long maxSizeBytes) {
        this.username = username;
        this.maxSizeBytes = maxSizeBytes;
        this.usedSpace = 0;
        this.root = new Directory("root", null);
        this.root.setProtectedDir(true);
        Directory shared = new Directory("shared", root);
        shared.setProtectedDir(true);
        this.root.addSubdirectory(shared);
        this.current = root;
    }

    public void rebuildParents() {
        rebuildParentsRecursive(root, null);
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

    public Directory getCurrent() {
        return current;
    }

    public void setCurrent(Directory current) {
        this.current = current;
    }
    
    public Directory searchDir(String nombre, Directory search) {
        if(search.getName().equals(nombre)) return search;
        for (Directory d : search.getSubdirectories()) {
            if (d.getName().equals(nombre)) {
                return d;
            } else {
                Directory found = searchDir(nombre, d);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    public Directory modSearchDir(String ruta, Directory actual) {
    if (ruta == null || ruta.isBlank()) return null;

    // Elimina "/" inicial si existe, y divide la ruta
    String[] partes = ruta.replaceFirst("^/", "").split("/");

    Directory dir = actual;

    for (String parte : partes) {
        if (parte.equals("root")) continue; // ya estamos en root
        Optional<Directory> sub = dir.getSubdirectories().stream()
            .filter(d -> d.getName().equals(parte))
            .findFirst();
        if (sub.isEmpty()) return null;
        dir = sub.get();
    }

    return dir;
}

}
