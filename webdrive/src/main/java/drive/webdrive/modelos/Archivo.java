package drive.webdrive.modelos;

import java.time.LocalDateTime;
import com.google.gson.annotations.JsonAdapter;

public class Archivo {
    private String name;
    private String extension;
    private String content;
    private int size;
    
    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime creationDate;
    
    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime modificationDate;

    public Archivo() {}

    public Archivo(String name, String extension, String content) {
        this.name = name;
        this.extension = extension;
        this.content = content;
        this.size = content.length();
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.modificationDate = LocalDateTime.now();
        this.size = content.length();
    }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { 
        this.modificationDate = modificationDate; 
    }
    // En la clase Archivo
@Override
public String toString() {
    return "Archivo{" +
            "name='" + name + '\'' +
            ", extension='" + extension + '\'' +
            ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 20)) + (content.length() > 20 ? "..." : "") : "null") + '\'' +
            ", size=" + size +
            ", creationDate=" + creationDate +
            ", modificationDate=" + modificationDate +
            '}';
}
}