package com.example.recycle.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Clasificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActividadClasificacion extends Activity {

    List<Clasificacion> clasificacion = new ArrayList<>();
    RecyclerView recyclerView;

    // Firestore
    private FirebaseFirestore db = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clasificacion);

        recyclerView = findViewById(R.id.recyclerClasificacion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (final DocumentSnapshot usuario : task.getResult().getDocuments()) {
                    db.collection("usuarios").document(usuario.getId()).collection("logros").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int puntuacionTotal = 0;

                                for (DocumentSnapshot logros : task.getResult().getDocuments()) {
                                    puntuacionTotal += Integer.parseInt(logros.get("progreso").toString());
                                }
                                clasificacion.add(new Clasificacion(usuario.get("nombre").toString(), puntuacionTotal));

                            clasificacion.sort(Comparator.comparing(Clasificacion::getPuntuacion).reversed());

                            AdaptadorClasificacion adaptadorClasificacion = new AdaptadorClasificacion(clasificacion);
                            recyclerView.setAdapter(adaptadorClasificacion);

                            for (int i = 0; i < clasificacion.size(); i++) {
                                if (clasificacion.get(i).getNombre().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                                    TextView posicion, nombre, puntuacion;
                                    posicion = findViewById(R.id.tuPosicion);
                                    nombre = findViewById(R.id.tuNombre);
                                    puntuacion = findViewById(R.id.tuPuntuacion);

                                    posicion.setText(String.valueOf(i + 1));
                                    nombre.setText(clasificacion.get(i).getNombre());
                                    puntuacion.setText(String.valueOf(clasificacion.get(i).getPuntuacion()));
                                }
                            }
                        }
                    });
                }
            }

        });


    }
}
