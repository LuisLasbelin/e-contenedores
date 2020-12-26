package com.example.recycle.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActividadConfirmarEditar extends Activity implements LocationListener {
    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    private Button guardar;
    private Button ubicacion;
    // Localizacion
    private LocationManager manejador;
    private String proveedor;
    String nombreCubo;

    String cuboID = null;
    Activity activity = null;

    private static final long TIEMPO_MIN = 10 * 1000; // 10 segundos
    private static final long DISTANCIA_MIN = 5; // 5 metros

    public Location posicion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_editar_cubo);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        cuboID = bundle.getString("cuboID");
        activity = this;
        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Cogemos los datos del cubo y rellenamos las casillas
        db.collection("cubos").document(cuboID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    EditText editTextNombre = findViewById(R.id.editTextNombre);
                    editTextNombre.setText(task.getResult().get("nombre").toString());
                }
            }
        });
        //Asignamos un listener al boton guardar ubicacion
        ubicacion = findViewById(R.id.btn_ubicacion);
        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si el usuario no existe, tomamos su posicion actual y la guardamos como default
                if(posicion != null) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("longitud", posicion.getLongitude());
                    datos.put("latitud", posicion.getLatitude());
                    db.collection("cubos").document(cuboID).update(datos);
                    Toast.makeText(getBaseContext(),"Se ha guardado la ubicacion con éxito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), R.string.gps_off, Toast.LENGTH_LONG).show();
                }
            }
        });
        // Asignamos un listener al boton guardar cubo
        guardar= findViewById(R.id.btn_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextNombre = findViewById(R.id.editTextNombre);
                nombreCubo = editTextNombre.getText().toString();
                Map<String, Object> nombre = new ArrayMap<>();
                nombre.put("nombre",nombreCubo);
                db.collection("cubos").document(cuboID).update(nombre);
                Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                    "Se han guardado los cambios", Toast.LENGTH_SHORT);
                    toast1.show();
                // reinicia main activity
                Intent i = new Intent(activity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(activity, MainActivity.class);
        activity.startActivity(i);
    }
    @SuppressLint("MissingPermission")
    @Override protected void onResume() {
        super.onResume();
        //Localizacion
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        posicion = manejador.getLastKnownLocation(proveedor);
        manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN,
                this);
    }
    @Override protected void onPause() {
        super.onPause();
        manejador.removeUpdates(this);
    }
    // Métodos de la interfaz LocationListener
    public void onLocationChanged(Location location) {

    }
    public void onProviderDisabled(String proveedor) {

    }
    public void onProviderEnabled(String proveedor) {

    }
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {

    }
}
