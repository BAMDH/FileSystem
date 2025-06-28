/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.modelos;

/**
 *
 * @author drayo
 */
import java.time.LocalDateTime;

public class Archivo {
    private String nombre;
    private String extension;
    private String contenido;
    private LocalDateTime creado;
    private LocalDateTime modificado;
    private int tamano;

    public Archivo() {}

    public Archivo(String nombre, String extension, String contenido) {
        this.nombre = nombre;
        this.extension = extension;
        this.contenido = contenido;
        this.creado = LocalDateTime.now();
        this.modificado = LocalDateTime.now();
        this.tamano = contenido.length();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) {
        this.contenido = contenido;
        this.modificado = LocalDateTime.now();
        this.tamano = contenido.length();
    }
    public LocalDateTime getCreado() { return creado; }
    public LocalDateTime getModificado() { return modificado; }
    public int getTamano() { return tamano; }
}
