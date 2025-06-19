/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servidor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
/**
 *
 * @author brand
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Archivo {
    private String nombre;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")    
    private Date fecha;

    public Archivo() {
    }

    public Archivo(String nombre) {
        this.nombre = nombre;
        fecha = new Date();
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
