package controller;

/**
 *
 * @author Psicops
 */
import java.util.List;
import modelo.Drive;
import modelo.Directory;
import modelo.File;

//import java.util.List;

public class Controller {
    private Drive drive;

    public Controller(Drive drive) {
        this.drive = drive;
    }

    public void crearDrive(String username, long maxSizeBytes) {
        this.drive = new Drive(username, maxSizeBytes);
        System.out.println("Creando Drive: " + username + " con espacio: " + maxSizeBytes + " bytes");
    }

    public void entrarDrive(String username) {
        // Cargar JSON
        System.out.println("Drive cargado: " + username);
    }

    public boolean existeFile(String nombre){
        List<File> files = drive.getCurrent().getFiles();
        for (File f : files) if (f.getName().equals(nombre)) return true;
        return false;
    }
    
    public void crearArchivo(String nombre, String extension, String contenido) {
        if (!drive.hasEnoughSpace(contenido.getBytes().length)) {
            System.out.println("No hay suficiente espacio disponible.");
            return;
        }
        if(!existeFile(nombre)){
            File file = new File(nombre, extension, contenido);
            drive.getCurrent().addFile(file);
            drive.useSpace(file.getSize());
            System.out.println("Archivo creado: " + nombre + "." + extension);
        } else {
            System.out.println("Ya existe un archivo con ese nombre.");
        }
    }

    public boolean existeDir(String nombre) {
        List<Directory> subdirectories = drive.getCurrent().getSubdirectories();
        for (Directory d : subdirectories) if (d.getName().equals(nombre)) return true;
        return false;
    }
    
    public void crearDirectorio(String nombre) {
        if (!existeDir(nombre)){
            Directory nuevo = new Directory(nombre, drive.getCurrent());
            drive.getCurrent().addSubdirectory(nuevo);
            System.out.println("Directorio creado: " + nombre);
        } else{
            System.out.println("Ya existe un directorio con este nombre.");
        }
    }

    public void cambiarDirectorio(String nombre) {
        if (nombre.equals("..")) {
            Directory parent = drive.getCurrent().getParent();
            if (parent != null) {
                drive.setCurrent(parent);
                System.out.println("Volviendo a: " + parent.getPath());
            }
        } else {
            Directory found = drive.getCurrent().getSubdirectory(nombre);
            if (found != null) {
                drive.setCurrent(found);
                System.out.println("Entrando a: " + found.getPath());
            } else {
                System.out.println("Directorio no encontrado.");
            }
        }
    }

    public void listarDirectorio() {
        Directory current = drive.getCurrent();
        System.out.println("Contenido de: " + current.getPath());
        for (Directory dir : current.getSubdirectories()) {
            System.out.println("[DIR] " + dir.getName());
        }
        for (File file : current.getFiles()) {
            System.out.println("[FILE] " + file.getFullName());
        }
    }

    public void modificarArchivo(String nombreCompleto, String nuevoContenido) {
        File file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            drive.freeSpace(file.getSize());
            file.setContent(nuevoContenido);
            drive.useSpace(file.getSize());
            System.out.println("Archivo modificado: " + nombreCompleto);
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    public void verPropiedades(String nombreCompleto) {
        File file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            System.out.println("Nombre: " + file.getFullName());
            System.out.println("Creacion: " + file.getCreationDate());
            System.out.println("Ultima modificacion: " + file.getModificationDate());
            System.out.println("Tamaño: " + file.getSize() + " bytes");
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    public void verArchivo(String nombreCompleto) {
        File file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            System.out.println("Contenido de " + nombreCompleto + ":");
            System.out.println(file.getContent());
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    public void copiarArchivo(String nombreCompleto, Directory destino) {
        File file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            File copia = new File(file.getFullName(), "", file.getContent());
            destino.addFile(copia);
            drive.useSpace(copia.getSize());
            System.out.println("Archivo copiado a: " + destino.getPath());
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    public void mover(String nombre, Directory destino) {
        Directory current = drive.getCurrent();
        File file = current.getFile(nombre);
        if (file != null) {
            current.getFiles().remove(file);
            destino.addFile(file);
            System.out.println("Archivo movido a: " + destino.getPath());
            return;
        }
        Directory dir = current.getSubdirectory(nombre);
        if (dir != null) {
            current.getSubdirectories().remove(dir);
            dir.setParent(destino);
            destino.addSubdirectory(dir);
            System.out.println("Directorio movido a: " + destino.getPath());
        }
    }

    public void loadArchivo(String nombre, String extension, String contenido) {
        crearArchivo(nombre, extension, contenido);
    }

    public void downloadArchivo(String nombreCompleto) {
        File file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            System.out.println("Simulando descarga de: " + file.getFullName());
        }
    }

    public void eliminar(String nombre, boolean recursivo) {
        Directory current = drive.getCurrent();
        File file = current.getFile(nombre);
        if (file != null) {
            current.getFiles().remove(file);
            drive.freeSpace(file.getSize());
            System.out.println("Archivo eliminado: " + nombre);
            return;
        }
        Directory dir = current.getSubdirectory(nombre);
        if (dir != null) {
            if (recursivo) {
                eliminarRecursivo(dir);
            }
            current.getSubdirectories().remove(dir);
            System.out.println("Directorio eliminado: " + nombre);
        }
    }

    private void eliminarRecursivo(Directory dir) {
        for (File file : dir.getFiles()) {
            drive.freeSpace(file.getSize());
        }
        for (Directory sub : dir.getSubdirectories()) {
            eliminarRecursivo(sub);
        }
    }

    public void compartir(String nombre, Drive otroUsuario) {
        File file = drive.getCurrent().getFile(nombre);
        if (file != null) {
            otroUsuario.getShared().addFile(file);
            System.out.println("Archivo compartido con: " + otroUsuario.getUsername());
        } else {
            System.out.println("Archivo no encontrado para compartir.");
        }
    }
}