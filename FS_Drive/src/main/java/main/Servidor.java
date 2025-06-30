package main;

import controller.Controller;
import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static controller.Controller.createFolder;
import java.util.ArrayList;
import java.util.List;
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
// ... importaciones y clase Servidor sin cambios ...

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

            String linea = entrada.readLine();
            if (linea == null) return;

            String[] partes = linea.split(" ");
            if (partes.length < 2) {
                escribirRespuesta(salidaRaw, 400, "{\"error\":\"Solicitud malformada\"}");
                return;
            }

            String metodo = partes[0];
            String ruta = partes[1];

            int contentLength = 0;
            String header;
            while ((header = entrada.readLine()) != null && !header.isEmpty()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }

            // /api/usuario/usuario
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
                escribirRespuesta(salidaRaw, 200, gson.toJson(userData));
                return;
            }

            // Leer cuerpo JSON para métodos POST
            char[] buffer = new char[contentLength];
            int leidos = 0;
            while (leidos < contentLength) {
                int actual = entrada.read(buffer, leidos, contentLength - leidos);
                if (actual == -1) break;
                leidos += actual;
            }
            String body = new String(buffer, 0, leidos);
            JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();

            // Variables comunes
            String usuario = jsonBody.get("usuario").getAsString();
            String rutaActual = jsonBody.has("rutaActual") ? jsonBody.get("rutaActual").getAsString() : "/root";
            String rutaDestino = jsonBody.has("rutaDestino") ? jsonBody.get("rutaDestino").getAsString() : "";

            List<String> archivos = new ArrayList<>();
            if (jsonBody.has("archivos")) {
                jsonBody.getAsJsonArray("archivos").forEach(el -> archivos.add(el.getAsString()));
            }
            List<String> carpetas = new ArrayList<>();
            if (jsonBody.has("carpetas")) {
                jsonBody.getAsJsonArray("carpetas").forEach(el -> carpetas.add(el.getAsString()));
            }

            Drive drive = FS.cargarDrive(usuario);
            if (drive == null) {
                escribirRespuesta(salidaRaw, 404, "{\"error\": \"Usuario no encontrado\"}");
                return;
            }

            Controller controller = new Controller(drive);
            controller.buscarDirectorio(rutaActual);
            boolean exito = false;

            switch (ruta) {
                case "/api/mover" -> {
                    for (String archivo : archivos) exito = controller.modMover(archivo, rutaDestino);
                    for (String carpeta : carpetas) exito = controller.modMover(carpeta, rutaDestino);
                    if (exito) {
                        FS.guardarDrive(drive);
                        escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Elementos movidos correctamente\"}");
                    } else {
                        escribirRespuesta(salidaRaw, 500, "{\"error\": \"Error al mover los elementos\"}");
                    }
                }

                case "/api/copiar" -> {
                    for (String archivo : archivos) exito = controller.modCopiarArchivo(archivo, rutaDestino);
                    for (String carpeta : carpetas) exito = controller.modCopiarArchivo(carpeta, rutaDestino);
                    if (exito) {
                        FS.guardarDrive(drive);
                        escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Elementos copiados correctamente\"}");
                    } else {
                        escribirRespuesta(salidaRaw, 500, "{\"error\": \"Error al copiar los elementos\"}");
                    }
                }

                case "/api/compartir" -> {
                    String receptor = jsonBody.get("usuarioDestino").getAsString();
                    for (String archivo : archivos) controller.compartir(archivo, receptor);
                    for (String carpeta : carpetas) controller.compartir(carpeta, receptor);
                    FS.guardarDrive(drive);
                    escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Elementos compartidos correctamente\"}");
                }

                case "/api/borrar" -> {
                    for (String archivo : archivos) controller.eliminar(archivo, false);
                    for (String carpeta : carpetas) controller.eliminar(carpeta, true);
                    FS.guardarDrive(drive);
                    escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Elementos eliminados correctamente\"}");
                }

                case "/api/guardar-archivo" -> {
                    String archivo = jsonBody.get("archivo").getAsString();
                    String contenido = jsonBody.get("contenido").getAsString();
                    controller.modificarArchivo(archivo, contenido);
                    FS.guardarDrive(drive);
                    escribirRespuesta(salidaRaw, 200, "{\"mensaje\": \"Archivo actualizado correctamente\"}");
                }

                case "/api/crear-carpeta" -> {
                    String nombreCarpeta = jsonBody.get("nombreCarpeta").getAsString();
                    boolean creada = createFolder(drive.getCurrent(), rutaDestino, nombreCarpeta);
                    if (creada) {
                        FS.guardarDrive(drive);
                        escribirRespuesta(salidaRaw, 200, "{\"mensaje\":\"Carpeta creada exitosamente\"}");
                    } else {
                        escribirRespuesta(salidaRaw, 400, "{\"error\":\"No se pudo crear la carpeta\"}");
                    }
                }

                default -> {
                    escribirRespuesta(salidaRaw, 404, "{\"error\": \"Ruta no encontrada\"}");
                }
            }

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
