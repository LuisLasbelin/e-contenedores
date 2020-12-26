package com.example.uartcomms;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivityUart extends Activity implements MqttCallback
{
    public String id = "yyZSlXEC8fBTKlSpRCti";
    public static MqttClient client = null;

    private ArrayList<Integer> distancias = new ArrayList<>();

    // Firestore
    private FirebaseFirestore db = null;

    private static final String BOTON_PIN = "BCM21"; //Puerto GPIO
    private Gpio botonGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectMqtt();

        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            botonGpio = manager.openGpio(BOTON_PIN); //crea conexión
            botonGpio.setDirection(Gpio.DIRECTION_IN);//es entrada
            botonGpio.setEdgeTriggerType(Gpio.EDGE_RISING);
            // 3. Habilita eventos de disparo por ambos flancos
            botonGpio.registerGpioCallback(null, callback); //registra callback
        } catch (IOException e) {
            Log.e("GPIO", "Error en PeripheralIO API", e);
        }
    }

    private GpioCallback callback = new GpioCallback() {
        @Override public boolean onGpioEdge(Gpio gpio) {

            try {
                Log.e("GPIO","cambio botón "+Boolean.toString(gpio.getValue()));
                try {
                    MqttMessage message = new MqttMessage("ON".getBytes());
                    message.setQos(Mqtt.qos);
                    message.setRetained(false);
                    client.publish(Mqtt.topicRoot + "POWER", message);
                } catch (MqttException e) {
                    Log.e(Mqtt.TAG, "Error al publicar.", e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // Tiempo hasta la siguiente posible interrupcion
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true; // 5. devolvemos true para mantener callback activo
        }
    };

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

        // Coloca los datos recibidos en el objeto datos
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

        // Sube los datos al Firebase
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