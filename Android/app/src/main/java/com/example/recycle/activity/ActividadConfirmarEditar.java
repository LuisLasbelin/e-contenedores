package com.example.recycle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActividadConfirmarEditar extends Activity {
    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    String cuboID = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_editar_cubo);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        cuboID = bundle.getString("cuboID");

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
    }

}
