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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Archivo {
    private String nombre;
    private String fecha;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
