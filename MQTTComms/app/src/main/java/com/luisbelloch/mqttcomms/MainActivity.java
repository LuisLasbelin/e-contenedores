package com.luisbelloch.mqttcomms;
// 192.168.137.198
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 * <p>
 * PeripheralManager manager = PeripheralManager.getInstance();
 * try {
 * Gpio gpio = manager.openGpio("BCM6");
 * gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * gpio.setValue(true);
 * } catch (IOException e) {
 * Log.e(TAG, "Unable to access GPIO");
 * }
 * <p>
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
public class MainActivity extends Activity implements MqttCallback {
    String uniqueID;
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
    Date date = new Date();
    DatosLeidos datos = new DatosLeidos();

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
            client.subscribe(Mqtt.topicRoot+"POWER", Mqtt.qos);
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

        if(topic.equals("recycle/practica/POWER") && payload.equals("ON")){

            uniqueID = UUID.randomUUID().toString();
            db.collection("cubos").document(id).collection("medidas").document(uniqueID)
                    .set(null)
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
        else{
            String datoCortado[];
            datoCortado = payload.split("-");

            Map<String, Object> datosGurdu = new HashMap<>();

            if(datoCortado[1].equals("CuboVidrio")){
                datos.setVidrio(datoCortado[2]);
            }
            else if(datoCortado[1].equals("CuboOrganico")){
                datos.setOrganico(datoCortado[2]);
            }
            else if(datoCortado[1].equals("CuboPlastico")){
                datos.setPlastico(datoCortado[2]);
            }
            else if(datoCortado[1].equals("CuboCarton")){
                datos.setCarton(datoCortado[2]);
            }
            datos.setFecha(date.getTime());
            datosGurdu.put(Long.toString(date.getTime()), datos);

            db.collection("cubos").document(id).collection("medidas").document(uniqueID)
                    .update(datosGurdu)
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
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }

}
