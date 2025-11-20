package com.example.gym.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Ejercicio {
    // Usamos alternate para aceptar "id" (de la lista general) o "ejercicioId" (de rutinas)
    @SerializedName(value = "id", alternate = {"ejercicioId"})
    private int ejercicioId;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("reps")
    private int reps;

    @SerializedName("peso")
    private double peso;

    private List<RutinaEjercicio> rutinaEjercicios;

    // 1. Constructor Vacío (Necesario para Gson/Retrofit)
    public Ejercicio() {
    }

    // 2. Constructor con Parámetros (NECESARIO PARA TU ERROR)
    public Ejercicio(int ejercicioId, String nombre, int reps, double peso) {
        this.ejercicioId = ejercicioId;
        this.nombre = nombre;
        this.reps = reps;
        this.peso = peso;
    }

    // Getters y Setters
    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public List<RutinaEjercicio> getRutinaEjercicios() {
        return rutinaEjercicios;
    }

    public void setRutinaEjercicios(List<RutinaEjercicio> rutinaEjercicios) {
        this.rutinaEjercicios = rutinaEjercicios;
    }
}