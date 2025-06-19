/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
/**
 *
 * @author brand
 */
public class FileSystem {
    private int puerto;
    private List<Drive> drives;
    private List<Usuario> usuarios;

    // Getters y Setters
    public int getPuerto() { return puerto; }
    public void setPuerto(int puerto) { this.puerto = puerto; }
    
    public List<Drive> getDrives() { return drives; }
    public void setDrives(List<Drive> drives) { this.drives = drives; }
    
    public Drive getDrive(String nombreDrive) {
    for (Drive drive : drives) {
        if (drive.getNombre().equalsIgnoreCase(nombreDrive)) {
            return drive;
        }
    }
    
    return null; // No encontrado
}
    
    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
}
