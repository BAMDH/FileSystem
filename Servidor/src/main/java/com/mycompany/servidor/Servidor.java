/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.servidor;


import java.io.*;
import java.net.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;      // Para manipular nodos
import com.fasterxml.jackson.core.JsonProcessingException;

/**public class Servidor {
    public static void main(String[] args) {
        FileSystemManager manager = new FileSystemManager("filesystem.json");
        
        // Acceder a datos
        System.out.println("Puerto: " + manager.getFileSystem().getPuerto());
        
        // Añadir un nuevo drive
        Drive nuevoDrive = new Drive();
        nuevoDrive.setNombre("prueba");
        nuevoDrive.setOwner("Nuevo Usuario");
        
        manager.addDrive(nuevoDrive);
      
    }
}**/

public class Servidor {
    
    public static void main(String[] args) throws IOException {
        FileSystemManager manager = new FileSystemManager("filesystem.json");

        int puerto = manager.getFileSystem().getPuerto();
        ServerSocket servidor = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en http://localhost:" + puerto);

        while (true) {
            Socket cliente = servidor.accept();
            new HiloCliente(cliente).start();
        }
    }
}


class HiloCliente extends Thread {
    private Socket socket;
    List<Drive> drives;
    FileSystemManager manager;
    public HiloCliente(Socket socket) {
        this.socket = socket;
        manager = new FileSystemManager("filesystem.json");
        drives=manager.getFileSystem().getDrives();
    }
    

    @Override
    
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String linea = entrada.readLine();
            if (linea == null || !linea.startsWith("GET")) return;

            String ruta = linea.split(" ")[1];
            String[] partes = ruta.split("/");

            salida.println("HTTP/1.1 200 OK");
            salida.println("Content-Type: text/html; charset=UTF-8");
            salida.println();
            salida.println("<html><body style='font-family:sans-serif;'>");

            if (partes.length <= 1 || partes[1].isEmpty()) {
                // Mostrar todos los drives
                salida.println("<h1>Drives disponibles</h1>");
                salida.println("<ul>");
                for (Drive drive : drives) {
                    salida.println("<li><a href='/" + drive.getNombre() + "'>" + drive.getNombre() + " (Propietario: " + drive.getOwner() + ")</a></li>");
                }
                salida.println("</ul>");
            } else {
                String nombreDrive = partes[1];
                Drive drive = manager.getFileSystem().getDrive(nombreDrive);

                if (drive == null) {
                    salida.println("<h1>Drive no encontrado: " + nombreDrive + "</h1>");
                } else {
                    List<Carpeta> carpetasActuales = drive.getCarpetas();
                    Carpeta carpetaActual = null;

                    for (int i = 2; i < partes.length; i++) {
                        String nombreCarpeta = partes[i];
                        carpetaActual = null;
                        for (Carpeta carpeta : carpetasActuales) {
                            if (carpeta.getNombre().equalsIgnoreCase(nombreCarpeta)) {
                                carpetaActual = carpeta;
                                carpetasActuales = carpeta.getCarpetas();
                                break;
                            }
                        }
                        if (carpetaActual == null) {
                            salida.println("<h1>Carpeta no encontrada: " + nombreCarpeta + "</h1>");
                            salida.println("</body></html>");
                            return;
                        }
                    }

                    // Mostrar contenido de la carpeta actual (o del drive si no hay carpetas)
                    salida.println("<h1>Contenido de: " + ruta + "</h1>");
                    salida.println("<ul>");

                    List<Carpeta> carpetas = (carpetaActual != null) ? carpetaActual.getCarpetas() : drive.getCarpetas();
                    List<Archivo> archivos = (carpetaActual != null) ? carpetaActual.getArchivos() : drive.getArchivos();

                    for (Carpeta sub : carpetas) {
                        salida.println("<li><a href='" + ruta + "/" + sub.getNombre() + "'>[Carpeta] " + sub.getNombre() + "</a></li>");
                    }

                    for (Archivo archivo : archivos) {
                        salida.println("<li>[Archivo] " + archivo.getNombre() + " - " + archivo.getFecha() + "</li>");
                    }

                    salida.println("</ul>");
                }
            }

            salida.println("</body></html>");
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

