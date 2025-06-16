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
public class Carpeta {
    private String nombre;
    private List<Archivo> archivos= new ArrayList<>();
    private List<Carpeta> carpetas= new ArrayList<>();

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public List<Archivo> getArchivos() { return archivos; }
    public void setArchivos(List<Archivo> archivos) { this.archivos = archivos; }
    
    public List<Carpeta> getCarpetas() { return carpetas; }
    public void setCarpetas(List<Carpeta> carpetas) { this.carpetas = carpetas; }
}
