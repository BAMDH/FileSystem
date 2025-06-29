package main;

import controller.Controller;
import modelo.Drive;

import java.util.Scanner;
import util.FS;

/**
 *
 * @author Psicops
 */
public class FS_Drive {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa nombre de usuario: ");
        String username = scanner.nextLine();
        Drive drive = util.FS.cargarDrive(username);
        if (drive == null) {
            System.out.print("Espacio maximo (bytes): ");
            long maxSize = Long.parseLong(scanner.nextLine());
            drive = new Drive(username, maxSize);
        } else {
            drive.rebuildParents();
        }
        Controller controller = new Controller(drive);
        System.out.println("\n=== File System ===");
        System.out.println("""
                           Seleccione el numero de comando que desea ejecutar seguido de los argumentos necesarios: 
                            1. Mostrar directorio
                            2. Crear directorio <nombre directorio>
                            3. Cambiar directorio <nombre directorio>
                            4. Crear archivo <nombre> <extension> <contenido>
                            5. Ver archivo <nombre.extension>
                            6. Modificar archivo <nombre.extension> <nuevo contenido>
                            7. Propiedades del un archivo <nombre.extension>
                            8. Eliminar archivo o directorio <nombre>
                            9. Copiar archivo <nombre.extension> <directorio destino>
                           10. Mover archivo o directorio <nombre> <directorio destino>
                           11. Compartir archivo <nombre> <usuario>
                           12. Subir archivo <ruta del archivo>
                           13. Descargar archivo <nombre> <ruta destino>
                           14. Salir
                           """);

        boolean running = true;
        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");

            switch (parts[0]) {
                case "1" -> controller.listarDirectorio();
                case "2" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 2 <nombre>");
                    } else {
                        controller.crearDirectorio(parts[1]);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "3" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 3 <directorio o .. para volver al anterior>");
                    } else {
                        controller.cambiarDirectorio(parts[1]);
                    }
                }
                case "4" -> {
                    if (parts.length < 4) {
                        System.out.println("Uso: 4 <nombre> <extension> <contenido>");
                    } else {
                        String nombre = parts[1];
                        String extension = parts[2];
                        String contenido = input.substring(input.indexOf(extension) + extension.length() + 1);
                        controller.crearArchivo(nombre, extension, contenido);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "5" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 5 <nombre.extension>");
                    } else {
                        controller.verArchivo(parts[1]);
                    }
                }
                case "6" -> {
                    if (parts.length < 3) {
                        System.out.println("Uso: 6 <nombre.extension> <nuevo contenido>");
                    } else {
                        String nombreCompleto = parts[1];
                        String nuevoContenido = input.substring(input.indexOf(nombreCompleto) + nombreCompleto.length() + 1);
                        controller.modificarArchivo(nombreCompleto, nuevoContenido);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "7" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 7 <nombre.extension>");
                    } else {
                        controller.verPropiedades(parts[1]);
                    }
                }
                case "8" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 8 <nombre.extension>");
                    } else {
                        boolean recursivo = parts.length > 2 && parts[2].equals("recursivo");
                        controller.eliminar(parts[1], recursivo);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "9" -> {
                    if (parts.length < 3) {
                        System.out.println("Uso: 9 <nombre.extension> <directorio destino>");
                    } else {
                        controller.copiarArchivo(parts[1], parts[2]);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "10" -> {
                    if (parts.length < 3) {
                        System.out.println("Uso: 10 <nombre> <directorio destino>");
                    } else {
                        controller.mover(parts[1], parts[2]);
                        util.FS.guardarDrive(drive);
                    }
                }
                case "11" -> {
                    if (parts.length < 3) {
                        System.out.println("Uso: 11 <nombre> <usuario>");
                    } else {
                        controller.compartir(parts[1], parts[2]);
                    }
                }
                case "12" -> {
                    if (parts.length < 2) {
                        System.out.println("Uso: 12 <ruta del archivo>");
                    } else {
                        controller.loadArchivo(parts[1]);
                    }
                }                
                case "13" -> {
                    if (parts.length < 3) {
                        System.out.println("Uso: 13 <nombre> <ruta destino>");
                    } else {
                        controller.downloadArchivo(parts[1], parts[2]);
                    }
                }                
                case "14" -> {
                    util.FS.guardarDrive(drive);
                    running = false;
                }
                default -> System.out.println("Comando no reconocido.");
            }
        }

        System.out.println("Saliendo del File System...");
    }
}