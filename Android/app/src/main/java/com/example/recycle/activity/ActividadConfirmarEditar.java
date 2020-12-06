package com.example.recycle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ActividadConfirmarEditar extends Activity {
    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    private Button guardar;
    String nombreCubo;

    String cuboID = null;
    Activity activity = null;

    @Override public void onCreate(Bundle savedInstanceState) {
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
                //actualizaCubos(vista);
                Intent i = new Intent(activity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                finish();
            }
        });


    }

}
