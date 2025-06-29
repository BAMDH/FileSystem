/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.Services;

/**
 *
 * @author drayo
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import drive.webdrive.modelos.*;
import drive.webdrive.utils.JsonManager;
import drive.webdrive.modelos.Directorio;
import drive.webdrive.modelos.UsuarioData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestTemplate;




@Service
public class DriveService {
    private final String SERVER_URL = "http://localhost:3000";

    public UsuarioData cargarUsuario(String nombre) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = SERVER_URL + "/api/usuario/" + nombre;
             // Obtener el JSON como string plano
        String json = restTemplate.getForObject(url, String.class);


        // Imprimir JSON bonito
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        JsonElement parsed = JsonParser.parseString(json);
       // System.out.println("=== JSON formateado ===");
       // System.out.println(gsonPretty.toJson(parsed));
        UsuarioData usuario = new Gson().fromJson(json, UsuarioData.class);
          //  System.out.print(usuario.getNombre());
           // System.out.println(usuario.toString());
            return usuario;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public void crearDirectorio(UsuarioData user, String nombreDir) {
        Directorio actual = user.getRaiz();
        if (actual.buscarSubdirectorio(nombreDir) == null) {
            actual.getSubdirectorios().add(new Directorio(nombreDir));

            // Enviar al servidor externo
            RestTemplate restTemplate = new RestTemplate();
            String url = SERVER_URL + "/api/crear-carpeta";
            Map<String, String> body = new HashMap<>();
            body.put("usuario", user.getNombre());
            body.put("nombre", nombreDir);
            restTemplate.postForObject(url, body, Void.class);
        }
    }*/
}