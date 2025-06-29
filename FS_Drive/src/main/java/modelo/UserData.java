package modelo;

public class UserData {
    private String nombre;
    private String rutaActual;
    private Directory raiz;

    public UserData(String nombre, String rutaActual, Directory raiz) {
        this.nombre = nombre;
        this.rutaActual = rutaActual;
        this.raiz = raiz;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaActual() {
        return rutaActual;
    }

    public Directory getRaiz() {
        return raiz;
    }
}
