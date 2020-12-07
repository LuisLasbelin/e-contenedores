package com.example.uartcomms;

public class Mqtt {
    public static final String TAG = "MQTT";
    public static final String topicRoot="recycle/practica/";
    //Reemplaza equipo por el nombre de tu equipo de prácticas
    public static final int qos = 1;
    public static final String broker = "tcp://mqtt.eclipse.org:1883";
    public static final String clientId = "Raspberry";
    //Reemplaza ClientId con un valor diferente
}