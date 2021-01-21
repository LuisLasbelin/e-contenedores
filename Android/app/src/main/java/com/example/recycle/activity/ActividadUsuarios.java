package com.example.recycle.activity;

import android.content.ClipData;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadUsuarios extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorUsuarios adaptador;

    public List<String> nombres = new ArrayList<>();
    public List<String> emails = new ArrayList<>();
    public List<String> cubos = new ArrayList<>();

    Map<String,Object> aux = new ArrayMap<>();
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        nombres.add(cortar(document.getData(),"nombre="));
                        emails.add(cortar(document.getData(),"mail="));
                        cubos.add(cortar(document.getData(),"cubos="));
                        Log.d("aaa1",cubos.toString());


                    }
                    setContentView(R.layout.usuarios);
                    recyclerView = findViewById(R.id.recyclerView);
                    adaptador = new AdaptadorUsuarios(ActividadUsuarios.this,  nombres, emails, cubos);
                    recyclerView.setAdapter(adaptador);
                    layoutManager = new LinearLayoutManager(ActividadUsuarios.this);
                    recyclerView.setLayoutManager(layoutManager);
                } else {
                    Log.w("aaa", "Error getting documents.", task.getException());
                }
            }
        });
    }


    public String cortar (Object datos, String patron){


        String[] aux = datos.toString().split(patron);
        String[] aux2;
        if (patron.equals("cubos=")){
            aux2 = aux[1].split("], ");
        }
        else {
            aux2 = aux[1].split(",");
        }

        if (aux2[0].contains("}")){
            aux2[0] = aux2[0].replace("}","");
        }
        return aux2[0];
    }
}

