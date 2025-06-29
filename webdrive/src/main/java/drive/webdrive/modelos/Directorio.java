package drive.webdrive.modelos;

import java.util.ArrayList;
import java.util.List;

public class Directorio {
    private String name;
    private List<Directorio> subdirectories = new ArrayList<>();
    private List<Archivo> files = new ArrayList<>();

    public Directorio() {}

    public Directorio(String name) {
        this.name = name;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Directorio> getSubdirectories() { return subdirectories; }
    public void setSubdirectories(List<Directorio> subdirectories) { 
        this.subdirectories = subdirectories; 
    }

    public List<Archivo> getFiles() { return files; }
    public void setFiles(List<Archivo> files) { this.files = files; }
    // En la clase Directorio
@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Directorio{")
      .append("name='").append(name).append('\'')
      .append(", subdirectories=[");
    
    if (subdirectories != null) {
        for (int i = 0; i < subdirectories.size(); i++) {
            sb.append(subdirectories.get(i).toString());
            if (i < subdirectories.size() - 1) {
                sb.append(", ");
            }
        }
    }
    
    sb.append("], files=[");
    
    if (files != null) {
        for (int i = 0; i < files.size(); i++) {
            sb.append(files.get(i).toString());
            if (i < files.size() - 1) {
                sb.append(", ");
            }
        }
    }
    
    sb.append("]}");
    return sb.toString();
}
}