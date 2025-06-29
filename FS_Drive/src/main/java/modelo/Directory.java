package modelo;

/**
 *
 * @author Psicops
 */
import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private transient Directory parent;
    private List<Directory> subdirectories;
    private List<File> files;

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

    public void addFile(File file) {
        files.add(file);
    }

    public void addSubdirectory(Directory dir) {
        subdirectories.add(dir);
    }

    public Directory getSubdirectory(String name) {
        for (Directory dir : subdirectories) {
            if (dir.name.equals(name)) return dir;
        }
        return null;
    }

    public File getFile(String name) {
        for (File file : files) {
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

    public List<Directory> getSubdirectories() {
        return subdirectories;
    }

    public void setSubdirectories(List<Directory> subdirectories) {
        this.subdirectories = subdirectories;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    
}