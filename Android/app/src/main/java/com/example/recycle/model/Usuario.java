package com.example.recycle.model;

import java.util.List;

public class Usuario {

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCubos() {
        return cubos;
    }

    public void setCubos(List<String> cubos) {
        this.cubos = cubos;
    }

    private String mail;
    private String name;
    private List<String> cubos;

    public Usuario(String mail, String name, List<String> cubos) {
        this.mail = mail;
        this.name = name;
        this.cubos = cubos;
    }
}
