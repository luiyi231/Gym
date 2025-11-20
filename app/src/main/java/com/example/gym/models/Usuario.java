package com.example.gym.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Usuario {
    // CAMBIO: "id" en minúscula para coincidir con JSON
    @SerializedName("id")
    private int id;

    // CAMBIO: "nombre" en minúscula
    @SerializedName("nombre")
    private String nombre;

    // CAMBIO: "apellido" en minúscula
    @SerializedName("apellido")
    private String apellido;

    private List<Rutina> rutinas;

    public Usuario() {}

    public Usuario(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // Getters y Setters iguales...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public List<Rutina> getRutinas() { return rutinas; }
    public void setRutinas(List<Rutina> rutinas) { this.rutinas = rutinas; }
}