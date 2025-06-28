/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.modelos;

/**
 *
 * @author drayo
 */
/*public class UsuarioData {
    private String username;
    //private String[] archivos;

    // Getters y setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

   
}*/


public class UsuarioData {
    private String nombre;
    private int espacioMaximo;
    private String rutaActual;
    private Directorio raiz;
    private Directorio compartidos;

    public UsuarioData() {}

    public UsuarioData(String nombre, int espacioMaximo) {
        this.nombre = nombre;
        this.espacioMaximo = espacioMaximo;
        this.rutaActual = "/";
        this.raiz = new Directorio("/");
        this.compartidos = new Directorio("compartidos");
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEspacioMaximo() { return espacioMaximo; }
    public void setEspacioMaximo(int espacioMaximo) { this.espacioMaximo = espacioMaximo; }
    public String getRutaActual() { return rutaActual; }
    public void setRutaActual(String rutaActual) { this.rutaActual = rutaActual; }
    public Directorio getRaiz() { return raiz; }
    public void setRaiz(Directorio raiz) { this.raiz = raiz; }
    public Directorio getCompartidos() { return compartidos; }
    public void setCompartidos(Directorio compartidos) { this.compartidos = compartidos; }
}


