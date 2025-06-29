package drive.webdrive.modelos;

import java.util.ArrayList;
import java.util.List;

public class UsuarioData {
    private String nombre;
    private String rutaActual;
    private Directorio raiz;

    public UsuarioData() {
        this.raiz = new Directorio();
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRutaActual() { return rutaActual; }
    public void setRutaActual(String rutaActual) { this.rutaActual = rutaActual; }

    public Directorio getRaiz() { return raiz; }
    public void setRaiz(Directorio raiz) { this.raiz = raiz; }
    // En la clase UsuarioData
@Override
    public String toString() {
        return "UsuarioData{" +
                "nombre='" + nombre + '\'' +
                ", rutaActual='" + rutaActual + '\'' +
                ", raiz=" + (raiz != null ? raiz.toString() : "null") +
                '}';
    }
}