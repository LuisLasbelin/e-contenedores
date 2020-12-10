package com.luisbelloch.mqttcomms;

public class DatosLeidos {

    private String organico;
    private String plastico;
    private String vidrio;
    private String carton;

    public String getOrganico() {
        return organico;
    }

    public void setOrganico(String organico) {
        this.organico = organico;
    }

    public String getPlastico() {
        return plastico;
    }

    public void setPlastico(String plastico) {
        this.plastico = plastico;
    }

    public String getVidrio() {
        return vidrio;
    }

    public void setVidrio(String vidrio) {
        this.vidrio = vidrio;
    }

    public String getCarton() {
        return carton;
    }

    public void setCarton(String carton) {
        this.carton = carton;
    }

    public DatosLeidos() {
        this.organico = "0";
        this.plastico = "0";
        this.vidrio = "0";
        this.carton = "0";
    }

    public DatosLeidos(String organico, String plastico, String vidrio, String carton) {
        this.organico = organico;
        this.plastico = plastico;
        this.vidrio = vidrio;
        this.carton = carton;
    }
}
