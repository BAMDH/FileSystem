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

public class Servidor {
    public static void main(String[] args) {
        FileSystemManager manager = new FileSystemManager("filesystem.json");
        
        // Acceder a datos
        System.out.println("Puerto: " + manager.getFileSystem().getPuerto());
        
        // Añadir un nuevo drive
        Drive nuevoDrive = new Drive();
        nuevoDrive.setNombre("nuevo_drive");
        nuevoDrive.setOwner("Nuevo Usuario");
        
        manager.addDrive(nuevoDrive);
        
        // Modificar un valor
        manager.getFileSystem().setPuerto(9090);
        manager.saveFileSystem();
    }
}
/**
public class Servidor {
    
    public static void main(String[] args) throws IOException {
        File archivo = new File("Servidor.json");
        if (!archivo.exists()) {
            System.out.println("Archivo no encontrado: " + archivo.getAbsolutePath());
            return;
        }

       

        // Guardar
     
        int puerto = 8080;
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

    public HiloCliente(Socket socket) {
        this.socket = socket;
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

            // Cabecera HTTP
            salida.println("HTTP/1.1 200 OK");
            salida.println("Content-Type: text/html; charset=UTF-8");
            salida.println(); // Línea vacía: fin de headers

            // Rutas
            switch (ruta) {
                case "/inicio/w":
                    salida.println("<h1>Página de inicio</h1><p>Bienvenido a /inicio/w</p>");
                    break;
                case "/share":
                    salida.println("<h1>Zona de Compartir</h1><p>Archivos compartidos aquí.</p>");
                    break;
                default:
                    salida.println("<h1>404</h1><p>Ruta no encontrada: " + ruta + "</p>");
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
**/

