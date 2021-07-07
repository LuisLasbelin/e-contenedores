package com.example.recycle.model;

public class Logro {

    private String nombre;
    private String descripcion;
    private int progreso;
    private int check;

    public Logro() {
    }

    public Logro(String nombre, String descripcion, int progreso, int check) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.check = check;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
