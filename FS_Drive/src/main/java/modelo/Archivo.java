package modelo;

/**
 *
 * @author Psicops
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Archivo {
    private String name;
    private String extension;
    private String content;
    private long size; // En bytes
    private String creationDate;
    private String modificationDate;

    public Archivo(String name, String extension, String content) {
        this.name = name;
        this.extension = extension;
        this.content = content;
        this.size = content.getBytes().length;
        this.creationDate = getCurrentTime();
        this.modificationDate = this.creationDate;
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public void setContent(String newContent) {
        this.content = newContent;
        this.size = newContent.getBytes().length;
        this.modificationDate = getCurrentTime();
    }

    public String getFullName() {
        return name + "." + extension;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public long getSize() {
        return size;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getContent() {
        return content;
    }

    public String getModificationDate() {
        return modificationDate;
    }
    
}