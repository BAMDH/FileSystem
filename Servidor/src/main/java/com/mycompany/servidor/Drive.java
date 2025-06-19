/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor;

/**
 *
 * @author brand
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Drive {
    private String nombre;
    private String owner;
    private List<Archivo> archivos= new ArrayList<>();
    private List<Carpeta> carpetas= new ArrayList<>();

    public Drive(String nombre, String owner) {
        this.nombre = nombre;
        this.owner = owner;
    }

    public Drive() {
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public List<Archivo> getArchivos() { return archivos; }
    public void setArchivos(List<Archivo> archivos) { this.archivos = archivos; }
    
    public Carpeta getCarpeta(String nombreDrive) {
        for (Carpeta carpeta : carpetas) {
            if (carpeta.getNombre().equalsIgnoreCase(nombreDrive)) {
                return carpeta;
            }
        }
        return null;
    }

    
    public List<Carpeta> getCarpetas() { return carpetas; }
    public void setCarpetas(List<Carpeta> carpetas) { this.carpetas = carpetas; }
}


