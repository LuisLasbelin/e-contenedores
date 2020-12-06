package com.example.recycle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cubo {
    private List<Map<String, Object>> medidas = new ArrayList<>();
    private String nombre;

    public Cubo(List<Map<String, Object>> medidas, String nombre) {
        this.medidas = medidas;
        this.nombre = nombre;
    }

    public List<Map<String, Object>> getMedidas() {
        return medidas;
    }

    public void setMedidas(List<Map<String, Object>> medidas) {
        this.medidas = medidas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
