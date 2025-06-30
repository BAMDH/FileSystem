package main;

import controller.Controller;
import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static controller.Controller.createFolder;
import modelo.Directory;
import modelo.Drive;
import modelo.UserData;
import util.FS;

public class Servidor {

    public static void main(String[] args) throws IOException {
        int puerto = 3000;
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
            OutputStream salidaRaw = socket.getOutputStream();
            PrintWriter salida = new PrintWriter(salidaRaw, true)
        ) {
            Gson gson = new Gson();
            
            // Confirmar conexión
           // System.out.println("Conexión aceptada: " + socket.getInetAddress());

            // Leer línea de solicitud
            String linea = entrada.readLine();
            if (linea == null) {
             //   System.out.println("No se recibió ninguna línea. Conexión cerrada.");
                return;
            }

          //  System.out.println("Línea de solicitud: " + linea);
            String[] partes = linea.split(" ");
            if (partes.length < 2) {
                //System.out.println("Solicitud malformada.");
                escribirRespuesta(salidaRaw, 400, "{\"error\":\"Solicitud malformada\"}");
                return;
            }

            String metodo = partes[0];
            String ruta = partes[1];
           // System.out.println("Método: " + metodo);
           // System.out.println("Ruta solicitada: " + ruta);

            // Leer headers
            int contentLength = 0;
            String header;
            while ((header = entrada.readLine()) != null && !header.isEmpty()) {
                //System.out.println("Header: " + header);
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }

            // Manejar rutas
            if (ruta.startsWith("/api/usuario/")) {
                String username = ruta.split("/")[3];

                Drive drive = FS.cargarDrive(username);
                if (drive == null) {
                    escribirRespuesta(salidaRaw, 404, "{\"error\": \"Usuario no encontrado\"}");
                    return;
                }

                drive.rebuildParents();
                Controller controller = new Controller(drive);

                Directory raiz = drive.getCurrent();
                String rutaActual = drive.getCurrent().getPath();

                UserData userData = new UserData(username, rutaActual, raiz);
                String json = gson.toJson(userData);

                escribirRespuesta(salidaRaw, 200, json);
                return;

            }  else if (metodo.equals("POST") && ruta.equals("/api/crear-carpeta")) {
    // Leer el cuerpo de la solicitud
             char[] buffer = new char[contentLength];
             int leidos = entrada.read(buffer, 0, contentLength);
             String body = new String(buffer, 0, leidos);

        try {
              JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
              String usuario = jsonBody.get("usuario").getAsString();
              String nombreCarpeta = jsonBody.get("nombreCarpeta").getAsString();
              String rutaDestino = jsonBody.get("rutaDestino").getAsString();

        // Cargar el drive (simplificado)
        Drive drive = FS.cargarDrive(usuario);
        if (drive == null) {
            escribirRespuesta(salidaRaw, 404, "{\"error\":\"Usuario no encontrado\"}");
            return;
        }

        // Crear la carpeta
        boolean creada = createFolder(drive.getCurrent(), rutaDestino, nombreCarpeta);

        if (creada) {
            // Guardar cambios
            FS.guardarDrive(drive);
            
            // Responder con éxito
            escribirRespuesta(salidaRaw, 200, "{\"mensaje\":\"Carpeta creada exitosamente\"}");
        } else {
            escribirRespuesta(salidaRaw, 400, "{\"error\":\"No se pudo crear la carpeta\"}");
        }
    } catch (Exception e) {
        escribirRespuesta(salidaRaw, 500, "{\"error\":\"Error interno del servidor\"}");
    }} else if (metodo.equals("POST") && ruta.equals("/api/guardar-archivo")) {
                char[] buffer = new char[contentLength];
                int leidos = 0;
                while (leidos < contentLength) {
                    int actual = entrada.read(buffer, leidos, contentLength - leidos);
                    if (actual == -1) break;
                    leidos += actual;
                }
                String body = new String(buffer, 0, leidos);
                System.out.println("Cuerpo recibido: " + body);

                JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
                String usuario = jsonBody.get("usuario").getAsString();
                String archivo = jsonBody.get("archivo").getAsString();
                String contenido = jsonBody.get("contenido").getAsString();
                 String rutaActual = jsonBody.get("rutaActual").getAsString();

                Drive drive = FS.cargarDrive(usuario);
                if (drive == null) {
                    escribirRespuesta(salidaRaw, 404, "{\"error\":\"Usuario no encontrado\"}");
                    return;
                }
                
              
                Controller controller = new Controller(drive);
                controller.buscarDirectorio(rutaActual);
                
                //boolean cambioExitoso = controller.buscarRuta(jsonBody.get("rutaActual").getAsString());
                //if (!cambioExitoso) {
                //    System.out.println("No se pudo cambiar a la ruta: " + jsonBody.get("rutaActual").getAsString());
                //    return;  // o manejar error según convenga
                //}
                

                
                controller.modificarArchivo(archivo, contenido);
                
                FS.guardarDrive(drive);

                escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Archivo actualizado correctamente\"}");
                return;
            }

            // Ruta no encontrada
            escribirRespuesta(salidaRaw, 404, "{\"error\": \"Ruta no encontrada\"}");

        } catch (Exception e) {
            System.err.println("Error procesando la solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void escribirRespuesta(OutputStream salida, int status, String json) throws IOException {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            default -> "Internal Server Error";
        };

        byte[] body = json.getBytes("UTF-8");
        String headers = "HTTP/1.1 " + status + " " + statusText + "\r\n"
                       + "Content-Type: application/json; charset=UTF-8\r\n"
                       + "Content-Length: " + body.length + "\r\n"
                       + "\r\n";
        salida.write(headers.getBytes("UTF-8"));
        salida.write(body);
        salida.flush();
    }
}

