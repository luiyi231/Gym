package com.example.gym.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RutinaResponse {
    @SerializedName("rutinaId")
    private int rutinaId;
    @SerializedName("rutinaNombre")
    private String rutinaNombre;
    @SerializedName("ejercicios")
    private List<Ejercicio> ejercicios;

    public RutinaResponse() {
    }

    public int getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public String getRutinaNombre() {
        return rutinaNombre;
    }

    public void setRutinaNombre(String rutinaNombre) {
        this.rutinaNombre = rutinaNombre;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }
}

