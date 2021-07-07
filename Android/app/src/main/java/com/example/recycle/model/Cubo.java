package com.example.recycle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cubo {
    private List<Map<String, Object>> medidas = new ArrayList<>();
    private String nombre;
    private double latitude, longitude;
    private String ID;

    public Cubo(List<Map<String, Object>> medidas, String nombre, String ID, double latitude, double longitude) {
        this.medidas = medidas;
        this.nombre = nombre;
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getID() {
        return ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
