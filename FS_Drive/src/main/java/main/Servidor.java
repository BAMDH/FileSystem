package main;

import controller.Controller;
import java.io.*;
import java.net.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
}class HiloCliente extends Thread {
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

            String metodo = linea.split(" ")[0];
            String ruta = linea.split(" ")[1];

            // Leer headers
            int contentLength = 0;
            String header;
            while (!(header = entrada.readLine()).isEmpty()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }

            if (ruta.startsWith("/api/usuario/")) {
                String username = ruta.split("/")[3];

                Drive drive = FS.cargarDrive(username);
                if (drive == null) {
                    String error = "{\"error\": \"Usuario no encontrado\"}";
                    escribirRespuesta(salidaRaw, 404, error);
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

            } else if (metodo.equals("POST") && ruta.equals("/api/crear-carpeta")) {
                char[] buffer = new char[contentLength];
                entrada.read(buffer);
                String body = new String(buffer);

                JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
                String usuario = jsonBody.get("usuario").getAsString();
                String nombre = jsonBody.get("nombre").getAsString();

                Drive drive = FS.cargarDrive(usuario);
                if (drive == null) {
                    String error = "{\"error\": \"Usuario no encontrado\"}";
                    escribirRespuesta(salidaRaw, 404, error);
                    return;
                }

                Controller controller = new Controller(drive);
                controller.crearDirectorio(nombre);
                //FS.guardarDrive(usuario, drive);

                String respuesta = "{\"mensaje\": \"Carpeta creada\"}";
                escribirRespuesta(salidaRaw, 200, respuesta);
                return;

            } else {
                String error = "{\"error\": \"Ruta no encontrada\"}";
                escribirRespuesta(salidaRaw, 404, error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void escribirRespuesta(OutputStream salida, int status, String json) throws IOException {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            default -> "Error";
        };

        byte[] body = json.getBytes("UTF-8");
        String headers =
            "HTTP/1.1 " + status + " " + statusText + "\r\n" +
            "Content-Type: application/json; charset=UTF-8\r\n" +
            "Content-Length: " + body.length + "\r\n" +
            "\r\n";
        salida.write(headers.getBytes("UTF-8"));
        salida.write(body);
        salida.flush();
    }
}
