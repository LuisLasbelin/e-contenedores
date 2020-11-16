package com.example.uartcomms;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivityUart extends Activity
{

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

 /*

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


  */

        Runnable r=new Runnable() {
            public void run() {
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

}