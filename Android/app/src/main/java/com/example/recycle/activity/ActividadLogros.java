package com.example.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Logro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActividadLogros extends Activity {

    List<Logro> logros = new ArrayList<>();
    List<Integer> progreso = new ArrayList<>();
    RecyclerView recyclerView;

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logros);
        recyclerView = findViewById(R.id.recyclerLogros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("usuarios").document(usuario.getEmail()).collection("logros").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> progresos = task.getResult().getDocuments();
                for (DocumentSnapshot progresoActual : progresos) {
                        progreso.add(Integer.parseInt(progresoActual.get("progreso").toString()));
                }

                db.collection("logros").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> datos = task.getResult().getDocuments();

                        int i = 0;
                        int cuantosLogros = 0;
                        for (DocumentSnapshot documento : datos) {
                            logros.add(new Logro(documento.getId(), documento.get("descripcion").toString(), progreso.get(i), Integer.parseInt(documento.get("check").toString())));
                            if (progreso.get(i) >= Integer.parseInt(documento.get("check").toString())) {
                                cuantosLogros++;
                            }
                            i++;
                        }

                        AdaptadorLogros adaptadorLogros = new AdaptadorLogros(logros);
                        recyclerView.setAdapter(adaptadorLogros);

                        TextView cuantoProgreso = findViewById(R.id.cuantoProgreso);
                        cuantoProgreso.setText(cuantosLogros*100/datos.size() + "%");
                    }
                });

            }
        });




    }

}
