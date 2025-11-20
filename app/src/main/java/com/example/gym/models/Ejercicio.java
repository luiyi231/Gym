package com.example.gym.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Ejercicio {
    // CAMBIO: Acepta "id" (lista general) O "ejercicioId" (lista dentro de rutina)
    @SerializedName(value = "id", alternate = {"ejercicioId"})
    private int ejercicioId;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("reps")
    private int reps;

    @SerializedName("peso")
    private double peso;

    private List<RutinaEjercicio> rutinaEjercicios;

    // Constructores, Getters y Setters...
    public int getEjercicioId() { return ejercicioId; }
    public void setEjercicioId(int ejercicioId) { this.ejercicioId = ejercicioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}