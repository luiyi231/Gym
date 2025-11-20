package com.example.gym.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Rutina {
    @SerializedName("id") // Antes era "Id"
    private int rutinaId;

    @SerializedName("nombre") // Antes era "Nombre"
    private String rutinaNombre;

    @SerializedName("usuarioId") // Antes era "UsuarioId"
    private int usuarioId;

    private Usuario usuario;
    private List<Ejercicio> ejercicios;

    // Constructores, Getters y Setters...
    // Asegúrate de que los getters/setters sigan apuntando a las variables privadas correctamente
    public int getRutinaId() { return rutinaId; }
    public void setRutinaId(int rutinaId) { this.rutinaId = rutinaId; }
    public String getRutinaNombre() { return rutinaNombre; }
    public void setRutinaNombre(String rutinaNombre) { this.rutinaNombre = rutinaNombre; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Ejercicio> ejercicios) { this.ejercicios = ejercicios; }
}