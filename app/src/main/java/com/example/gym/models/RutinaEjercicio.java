package com.example.gym.models;

public class RutinaEjercicio {
    private int rutinaId;
    private int ejercicioId;
    private Rutina rutina;
    private Ejercicio ejercicio;

    public RutinaEjercicio() {
    }

    public RutinaEjercicio(int rutinaId, int ejercicioId) {
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
    }

    public int getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }
}

