package com.mycompany.servidor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author brand
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FileSystemManager {
    private FileSystem fileSystem;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String filePath;

    public FileSystemManager(String filePath) {
        this.filePath = filePath;
        loadFileSystem();
    }

    private void loadFileSystem() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                fileSystem = objectMapper.readValue(file, FileSystem.class);
            } else {
                fileSystem = new FileSystem();
                fileSystem.setPuerto(8080);
                saveFileSystem(); // Crea el archivo
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el filesystem: " + e.getMessage());
            // Puedes inicializar con valores por defecto en caso de error
            fileSystem = new FileSystem();
            fileSystem.setPuerto(8080);
        }
    }

    public void saveFileSystem() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                      .writeValue(new File(filePath), fileSystem);
        } catch (IOException e) {
            System.err.println("Error al guardar el filesystem: " + e.getMessage());
        }
    }

    
    public FileSystem getFileSystem() {
        return fileSystem;
    }

 
    public void addDrive(Drive drive) {
        if (fileSystem.getDrives() == null) {
            fileSystem.setDrives(new ArrayList<>());
        }
        fileSystem.getDrives().add(drive);
        saveFileSystem(); // Guarda después de modificar
    }

}
