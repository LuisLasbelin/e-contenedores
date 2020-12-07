package com.example.uartcomms;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityUart extends Activity implements MqttCallback
{
/*
    private static final String TAG = MainActivityUart.class.getSimpleName();
    ArduinoUart uart;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        uart = new ArduinoUart("UART0", 115200);

        Log.d(TAG, "Mandado a Arduino: H");
        uart.escribir("H");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }



        String s = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + s);

        Log.d(TAG, "Mandado a Arduino: D");
        uart.escribir("D");

        try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.w(TAG, "Error en sleep()", e);
        }
        s = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + s);


        Runnable r=new Runnable() {
    /*        public void run() {
                String s;
                uart.escribir("D");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "Error en sleep()", e);
                }
                s = uart.leer();
                Log.d(TAG, "Recibido de Arduino: " + s);

                String[] datosLectura = s.split(",");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> datos = new HashMap<>();
                datos.put("Lectura Arduino 1", datosLectura[0]);
                datos.put("Lectura Arduino 2", datosLectura[1]);
                datos.put("Lectura Arduino 3", datosLectura[2]);
                datos.put("Lectura Arduino 4", datosLectura[3]);
                db.collection("Sensor").document("Distancia").set(datos);


                handler.postDelayed(this, 500);
            }
        };

        handler.postDelayed(r, 500);
    }


    @Override protected void onDestroy() {
        super.onDestroy();
    }


     */
    public String id = "BMWkJhGML689IJwudXXU";
    public static MqttClient client = null;

    private ArrayList<Integer> distancias = new ArrayList<>();

    // Firestore
    private FirebaseFirestore db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectMqtt();
/*
        try {
            String text = "ON";
            byte[] bytes = text.getBytes("UTF-8");
            text = new String(bytes, "UTF-8");
            client.publish(Mqtt.topicRoot + "cmnd/POWER", new MqttMessage(bytes));
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
 */
    }

    public void connectMqtt() {
        try {
            Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
            client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al conectar.", e);
        }

        try {
            Log.i(Mqtt.TAG, "Suscrito a " + Mqtt.topicRoot+"distancia");
            client.subscribe(Mqtt.topicRoot+"distancia", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
    }

    // Desconexión
    @Override public void onDestroy() {
        try {
            Log.i(Mqtt.TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al desconectar.", e);
        }
        super.onDestroy();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);


        // Firestore initialization
        db = FirebaseFirestore.getInstance();
        String datoCortado[];
        datoCortado = payload.split("-");
        Map<String, Object> datos = new HashMap<>();

        if(datoCortado[2].equals("CuboVidrio")){
            datos.put("vidrio", payload);
        }
        else if(datoCortado[2].equals("CuboOrganico")){
            datos.put("organico", payload);
        }
        else if(datoCortado[2].equals("CuboPlastico")){
            datos.put("plastico", payload);
        }
        else if(datoCortado[2].equals("CuboCarton")){
            datos.put("carton", payload);
        }

        db.collection("cubos").document()
                .update(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing document", e);
                    }
                });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }

}