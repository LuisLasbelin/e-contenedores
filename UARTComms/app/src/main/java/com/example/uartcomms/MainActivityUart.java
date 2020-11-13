package com.example.uartcomms;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class MainActivityUart extends Activity
{

    private static final String TAG = MainActivityUart.class.getSimpleName();
    ArduinoUart uart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        uart = new ArduinoUart("UART0", 115200);
        Log.d(TAG, "Mandado a Arduino: H");
        uart.escribir("H");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }

        String s = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + s);

        Log.d(TAG, "Mandado a Arduino: D");
        uart.escribir("D");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }

        s = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + s);
    }


    @Override protected void onDestroy() {
        super.onDestroy();
    }

}