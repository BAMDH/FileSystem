/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.modelos;

/**
 *
 * @author drayo
 */
import java.util.*;

public class Directorio {
    private String nombre;
    private List<Archivo> archivos = new ArrayList<>();
    private List<Directorio> subdirectorios = new ArrayList<>();

    public Directorio() {}

    public Directorio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Archivo> getArchivos() { return archivos; }
    public List<Directorio> getSubdirectorios() { return subdirectorios; }

    public Directorio buscarSubdirectorio(String nombre) {
        for (Directorio dir : subdirectorios) {
            if (dir.getNombre().equals(nombre)) return dir;
        }
        return null;
    }
}

