/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.utils;

/**
 *
 * @author drayo
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import drive.webdrive.modelos.UsuarioData;

import java.io.*;

public class JsonManager {
    private static final String BASE_PATH = "user_data/";

    public static void guardar(UsuarioData data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(BASE_PATH + data.getNombre() + ".json"), data);
    }

    public static UsuarioData cargar(String nombre) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(BASE_PATH + nombre + ".json"), UsuarioData.class);
    }
}