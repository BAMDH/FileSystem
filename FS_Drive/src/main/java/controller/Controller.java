package controller;

/**
 *
 * @author Psicops
 */
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import modelo.Drive;
import modelo.Directory;
import modelo.Archivo;
import util.FS;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public boolean existeFile(String nombre) {
        List<Archivo> files = drive.getCurrent().getFiles();
        for (Archivo f : files) {
            if (f.getName().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void crearArchivo(String nombre, String extension, String contenido) {
        if (!drive.hasEnoughSpace(contenido.getBytes().length)) {
            System.out.println("No hay suficiente espacio disponible.");
            return;
        }
        if (!existeFile(nombre)) {
            Archivo file = new Archivo(nombre, extension, contenido);
            drive.getCurrent().addFile(file);
            drive.useSpace(file.getSize());
            System.out.println("Archivo creado: " + nombre + "." + extension);
        } else {
            System.out.println("Ya existe un archivo con ese nombre.");
        }
    }

    public boolean existeDir(String nombre) {
        List<Directory> subdirectories = drive.getCurrent().getSubdirectories();
        for (Directory d : subdirectories) {
            if (d.getName().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void crearDirectorio(String nombre) {
        Directory actual = drive.getCurrent();
        if (!existeDir(nombre)) {
            Directory nuevo = new Directory(nombre, actual);
            actual.addSubdirectory(nuevo);
            drive.rebuildParents();
            System.out.println("Directorio creado: " + nombre);
        } else {
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

    public void buscarDirectorio(String ruta) {

        String sinRoot = ruta.replaceFirst("^/root", "");

// Dividir por "/"
        String[] partes = sinRoot.split("/");

// Recorrer los nombres (ignorando vacíos)
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                cambiarDirectorio(parte);
            }
        }

    }

    public void listarDirectorio() {
        Directory current = drive.getCurrent();
        System.out.println("Contenido de: " + current.getPath());
        for (Directory dir : current.getSubdirectories()) {
            System.out.println("[DIR] " + dir.getName());
        }
        for (Archivo file : current.getFiles()) {
            System.out.println("[FILE] " + file.getFullName());
        }
    }

    public void modificarArchivo(String nombreCompleto, String nuevoContenido) {
        Archivo file = drive.getCurrent().getFile(nombreCompleto);
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
        Archivo file = drive.getCurrent().getFile(nombreCompleto);
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
        Archivo file = drive.getCurrent().getFile(nombreCompleto);
        if (file != null) {
            System.out.println("Contenido de " + nombreCompleto + ":");
            System.out.println(file.getContent());
        } else {
            System.out.println("Archivo no encontrado.");
        }
    }

    public boolean copiarArchivo(String nombreCompleto, String dirDestino) {
        Archivo file = drive.getCurrent().getFile(nombreCompleto);
        Directory destino = drive.searchDir(dirDestino, drive.getRoot());
        if (file != null && destino != null) {
            if (destino.getFile(nombreCompleto) == null) {
                Archivo copia = new Archivo(file.getName(), file.getExtension(), file.getContent());
                destino.addFile(copia);
                drive.useSpace(copia.getSize());
                System.out.println("Archivo copiado a: " + destino.getPath());
                return true;
            } else {
                System.out.println("Ya existe un archivo con este nombre en el directorio seleccionado.");
                return false;
            }
        } else {
            System.out.println("Archivo no encontrado o el directorio no existe.");
            return false;
        }
    }
    public boolean modCopiarArchivo(String nombreCompleto, String dirDestinoRuta) {
   
    Directory actual = drive.getCurrent();
    Archivo file = actual.getFile(nombreCompleto);
    
        System.out.println(nombreCompleto);
        System.out.println(dirDestinoRuta);
        System.out.println(file);
        System.out.println(actual.getName());
    Directory destino = drive.modSearchDir(dirDestinoRuta, drive.getRoot());

    if (file != null && destino != null) {
        // 3. Verificar si ya existe un archivo con ese nombre
        if (destino.getFile(nombreCompleto) == null) {
            Archivo copia = new Archivo(file.getName(), file.getExtension(), file.getContent());
            destino.addFile(copia);
            drive.useSpace(copia.getSize());

            System.out.println("Archivo copiado a: " + destino.getPath());
            return true;
        } else {
            System.out.println("Ya existe un archivo con este nombre en el directorio seleccionado.");
            return false;
        }
    } else {
        System.out.println("Archivo no encontrado o el directorio no existe.");
        return false;
    }
}

public boolean modMover(String nombre, String dirDestinoRuta) {
    Directory current = drive.getCurrent(); // carpeta actual del usuario
    Archivo file = current.getFile(nombre);

    Directory destino = drive.modSearchDir(dirDestinoRuta, drive.getRoot());

    if (destino == null) {
        System.out.println("No se ha encontrado el directorio destino.");
        return false;
    }

    if (file != null) {
        current.getFiles().remove(file);
        destino.addFile(file);
        System.out.println("Archivo movido a: " + destino.getPath());
        return true;
    }

    Directory dir = current.getSubdirectory(nombre);
    if (dir != null) {
        if (dir.isProtected()) {
            System.out.println("No se puede mover una carpeta protegida.");
            return false;
        }

        current.getSubdirectories().remove(dir);
        dir.setParent(destino);
        destino.addSubdirectory(dir);
        System.out.println("Directorio movido a: " + destino.getPath());
        return true;
    }

    System.out.println("No se ha encontrado un archivo o directorio con ese nombre.");
    return false;
}

    public boolean mover(String nombre, String dirDestino) {
        Directory current = drive.getCurrent();
        Archivo file = current.getFile(nombre);
        Directory destino = drive.searchDir(dirDestino, drive.getRoot());
        if (destino != null) {
            if (file != null) {
                current.getFiles().remove(file);
                destino.addFile(file);
                System.out.println("Archivo movido a: " + destino.getPath());
                return false;
            }
            Directory dir = current.getSubdirectory(nombre);
            if (dir != null) {
                if (dir.isProtected()) {
                    System.out.println("No se puede mover una carpeta protegida.");
                    return false;
                }
                current.getSubdirectories().remove(dir);
                dir.setParent(destino);
                destino.addSubdirectory(dir);
                System.out.println("Directorio movido a: " + destino.getPath());
                return true;
            }
            System.out.println("No se ha encontrado un archivo o directorio con ese nombre.");
        } else {
            System.out.println("No se ha encontrado el directorio destino.");
            return false;
        }
        return false;
    }

    public void loadArchivo(String rutaLocal) {
        try {
            File file = new File(rutaLocal);

            if (!file.exists()) {
                System.out.println("El archivo no existe: " + rutaLocal);
                return;
            }

            String contenido = Files.readString(Path.of(rutaLocal));
            String nombre = file.getName();
            if (!nombre.toLowerCase().endsWith(".txt")) {
                System.out.println("Solo se permiten archivos con extensión .txt");
                return;
            }
            String baseName = nombre.contains(".")
                    ? nombre.substring(0, nombre.lastIndexOf('.'))
                    : nombre;
            String extension = nombre.contains(".")
                    ? nombre.substring(nombre.lastIndexOf('.') + 1)
                    : "";

            if (!drive.hasEnoughSpace(contenido.getBytes().length)) {
                System.out.println("No hay espacio suficiente para cargar este archivo.");
                return;
            }

            Archivo nuevo = new Archivo(baseName, extension, contenido);
            drive.getCurrent().addFile(nuevo);
            drive.useSpace(nuevo.getSize());

            System.out.println("Archivo cargado exitosamente: " + nombre);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo local: " + e.getMessage());
        }
    }

    public boolean downloadArchivo(String nombreCompleto, String rutaDestino) {
        Archivo file = drive.getCurrent().getFile(nombreCompleto);
        if (file == null) {
            System.out.println("No se encontró el archivo: " + nombreCompleto);
            return false;
        }

        if (!file.getExtension().equalsIgnoreCase("txt")) {
            System.out.println("Solo se pueden descargar archivos .txt");
            return false;
        }

        File destino = new File(rutaDestino);
        String rutaFinal;

        if (destino.isDirectory()) {
            rutaFinal = destino.getAbsolutePath() + File.separator + file.getFullName();
        } else {
            rutaFinal = rutaDestino;
        }

        try (FileWriter writer = new FileWriter(rutaFinal)) {
            writer.write(file.getContent());
            System.out.println("Archivo descargado en: " + rutaFinal);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
            return false;
        }
    }

    public void eliminar(String nombre, boolean recursivo) {
        Directory current = drive.getCurrent();
        Archivo file = current.getFile(nombre);
        if (file != null) {
            current.getFiles().remove(file);
            drive.freeSpace(file.getSize());
            System.out.println("Archivo eliminado: " + nombre);
            return;
        }
        if (current.getSubdirectory(nombre) == null) {
            Directory dir = drive.searchDir(nombre, drive.getRoot());
            if (dir != null) {
                if (dir.isProtected()) {
                    System.out.println("No se puede eliminar un directorio protegido.");
                    return;
                }
                if (recursivo) {
                    eliminarRecursivo(dir);
                }
                dir.getParent().getSubdirectories().remove(dir);
                System.out.println("Directorio eliminado: " + nombre);
                drive.goToRoot();
            }
        } else {
            Directory dir = current.getSubdirectory(nombre);
            if (dir.isProtected()) {
                System.out.println("No se puede eliminar un directorio protegido.");
                return;
            }
            current.getSubdirectories().remove(dir);
            System.out.println("Directorio eliminado: " + nombre);
        }
    }

    private void eliminarRecursivo(Directory dir) {
        for (Archivo file : dir.getFiles()) {
            drive.freeSpace(file.getSize());
        }
        for (Directory sub : dir.getSubdirectories()) {
            eliminarRecursivo(sub);
        }
    }

    public void compartir(String nombre, String receptorUsername) {
        Archivo file = drive.getCurrent().getFile(nombre);
        Drive receptor = FS.cargarDrive(receptorUsername);
        if (receptor == null) {
            System.out.println("El usuario receptor no existe.");
            return;
        }
        receptor.rebuildParents();
        Directory shared = receptor.getRoot().getSubdirectory("shared");
        if (file != null) {
            Archivo copia = new Archivo(file.getName(), file.getExtension(), file.getContent());
            shared.addFile(copia);
            FS.guardarDrive(receptor);
            System.out.println("Archivo compartido con: " + receptorUsername);
            return;
        }
        Directory dirShare = drive.searchDir(nombre, drive.getRoot());
        if (dirShare != null) {
            Directory newDir = new Directory(dirShare.getName(), shared);
            newDir.setFiles(dirShare.getFiles());
            newDir.setSubdirectories(dirShare.getSubdirectories());
            shared.addSubdirectory(newDir);
            FS.guardarDrive(receptor);
            System.out.println("Directorio compartido con: " + receptorUsername);
            return;
        }
        System.out.println("No se ha encontrado el archivo o directorio.");
    }
    
      
public static boolean createFolder(Directory currentDirectory, String path, String folderName) {
    try {
        // 1. Navegar al directorio destino
        Directory targetDir = currentDirectory;
        
        if (path != null && !path.isEmpty()) {
            String[] parts = path.split("/");
            for (String part : parts) {
                if (part.isEmpty()) continue;
                
                boolean found = false;
                for (Directory subdir : targetDir.getSubdirectories()) {
                    if (subdir.getName().equals(part)) {
                        targetDir = subdir;
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    System.out.println("No se encontró el directorio: " + part);
                    return false;
                }
            }
        }

        // 2. Verificar si ya existe
        for (Directory dir : targetDir.getSubdirectories()) {
            if (dir.getName().equals(folderName)) {
                System.out.println("Ya existe una carpeta con ese nombre");
                return false;
            }
        }

        // 3. Crear la nueva carpeta
        Directory newFolder = new Directory(folderName, targetDir);
        targetDir.getSubdirectories().add(newFolder);
        
        System.out.println("Carpeta creada exitosamente en: " + targetDir.getPath() + "/" + folderName);
        return true;
        
    } catch (Exception e) {
        System.out.println("Error al crear carpeta: " + e.getMessage());
        return false;
    }
}

    public static Directory navigateToDirectory(Directory current, String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return current;
        }

        String[] parts = path.split("/");
        Directory temp = current;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            boolean found = false;
            for (Directory dir : temp.getSubdirectories()) {
                if (dir.getName().equals(part)) {
                    temp = dir;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return null;
            }
        }
        
        return temp;
    }
    

}
