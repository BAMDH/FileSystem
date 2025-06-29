package modelo;

/**
 *
 * @author Psicops
 */
import java.util.ArrayList;

public class Directory {
    private String name;
    private transient Directory parent;
    private ArrayList<Directory> subdirectories;
    private ArrayList<Archivo> files;
    private boolean protectedDir = false;

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.subdirectories = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public String getPath() {
        if (parent == null) return "/" + name;
        return parent.getPath() + "/" + name;
    }

    public void addFile(Archivo file) {
        files.add(file);
    }

    public void addSubdirectory(Directory dir) {
        subdirectories.add(dir);
        dir.setParent(this);
    }

    public Directory getSubdirectory(String name) {
        for (Directory dir : subdirectories) {
            if (dir.name.equals(name)) return dir;
        }
        return null;
    }

    public Archivo getFile(String name) {
        for (Archivo file : files) {
            if (file.getFullName().equals(name)) return file;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public ArrayList<Directory> getSubdirectories() {
        return subdirectories;
    }

    public void setSubdirectories(ArrayList<Directory> subdirectories) {
        this.subdirectories = subdirectories;
    }

    public ArrayList<Archivo> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<Archivo> files) {
        this.files = files;
    }

    public void setProtectedDir(boolean protectedDir) {
        this.protectedDir = protectedDir;
    }
    
    public boolean isProtected(){
        return this.protectedDir;
    }

}