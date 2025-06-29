package util;

/**
 *
 * @author Psicops
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modelo.Drive;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FS {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void guardarDrive(Drive drive) {
        String filename = drive.getUsername() + "_drive.json";
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(drive, writer);
            System.out.println("Drive guardado en: " + filename);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static Drive cargarDrive(String username) {
        String filename = username + "_drive.json";
        try (FileReader reader = new FileReader(filename)) {
            Drive drive = gson.fromJson(reader, Drive.class);
            System.out.println("Drive cargado desde: " + filename);
            drive.goToRoot();
            return drive;
        } catch (IOException e) {
            System.out.println("Usuario no encontrado.\nCreando nuevo usuario....");
            return null;
        }
    }
}