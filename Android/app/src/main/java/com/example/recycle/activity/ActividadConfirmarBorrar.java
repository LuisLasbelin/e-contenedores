package com.example.recycle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActividadConfirmarBorrar extends Activity {

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    String cuboID = null;

    Activity activity = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_borrar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        cuboID = bundle.getString("cuboID");

        activity = this;
    }

    // Eliminar un cubo
    public void eliminarCubo(View view) {
        // Recogemos el check para ver si eliminamos también los registros

        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference userRef = db.collection("usuarios").document(usuario.getEmail());
        final CollectionReference medidasRef = db.collection("cubos").document(cuboID).collection("medidas");

        // Remove the 'medidas' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put("cubos", FieldValue.arrayRemove(cuboID));

        userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(activity, R.string.cubo_eliminado, Toast.LENGTH_LONG).show();
                    // Se reinicia Main Activity
                    activity.finish();
                    Intent i = new Intent(activity, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(i);
                    finish();
                }
            }
        });
        finish();
    }

    // Cancelar eliminar cubo
    public void eliminarCancelar(View view) {
        finish();
    }
}
