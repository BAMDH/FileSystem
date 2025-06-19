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
import java.util.List;


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

    private Carpeta obtenerCarpetaPorRuta(Drive drive, String[] rutaPartes, int indice) {
    Carpeta actual = null;

    for (int i = 1; i < rutaPartes.length; i++) {
        String nombreCarpeta = rutaPartes[i];
        actual = null;//si se vuelve a llegar es porque no la encontro
        actual = drive.getCarpeta(nombreCarpeta);//busca la carpeta
        if (actual == null) return null;//falla la busqueda
    }
    return actual;
    }
 
    public void addDrive(Drive drive) {
        if (fileSystem.getDrives() == null) {
            fileSystem.setDrives(new ArrayList<>());
        }
        fileSystem.getDrives().add(drive);
        saveFileSystem(); 
    }
    public void addArchivo(Archivo archivo, String nombreDrive) {
        fileSystem.getDrive(nombreDrive).getArchivos().add(archivo);
        saveFileSystem(); 
    }
    public void addCarpeta(Carpeta carpeta, String nombreDrive) {
        fileSystem.getDrive(nombreDrive).getCarpetas().add(carpeta);
        saveFileSystem(); 
    }

}
