package com.example.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActividadContenedoresCalle extends Activity {

    // Firestore
    private FirebaseFirestore db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedores_calle);
        db = FirebaseFirestore.getInstance();

        final Spinner spinner = (Spinner) findViewById(R.id.selectorTipo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button añadir = findViewById(R.id.añadirContenedor);
        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView latitud, longitud;
                latitud = findViewById(R.id.latitudContenedor);
                longitud = findViewById(R.id.longitudContenedor);
                if (!Double.isNaN(Double.parseDouble(latitud.getText().toString())) && !Double.isNaN(Double.parseDouble(longitud.getText().toString()))) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("latitud", Double.parseDouble(latitud.getText().toString()));
                    datos.put("longitud", Double.parseDouble(longitud.getText().toString()));
                    datos.put("tipo", spinner.getSelectedItem().toString());

                    db.collection("contenedores").document().set(datos);
                    Toast.makeText(getApplicationContext(), "Datos subidos a Firebase", Toast.LENGTH_LONG).show();
                    latitud.setText("");
                    longitud.setText("");
                    spinner.setSelection(0);
                } else {
                    Toast.makeText(getApplicationContext(), "La latitud o la longitud no son correctas. Por favor, inténtalo de nuevo", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
