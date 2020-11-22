package com.example.recycle.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdaptadorCubos extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<String> nombres = new ArrayList<String>();
    private ArrayList<String> carton = new ArrayList<String>();
    private ArrayList<String> vidrio = new ArrayList<String>();
    private ArrayList<String> plastico = new ArrayList<String>();
    private ArrayList<String> organico = new ArrayList<String>();
    private ArrayList<String> cubos = new ArrayList<String>();
    private String TAG = "cubos";
    private int items = 1;
    private int itemList = 0;

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    public AdaptadorCubos() {

        inicializarUsuario();
        inicializacion();
    }

    // Se revisa si el usuario tiene cubos añadadidos
    public void inicializarUsuario(){
        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("usuarios").document(usuario.getEmail()).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e){
                        if (e != null){
                            Log.e("Firebase", "Error al leer", e);
                        }  else if (snapshot == null || !snapshot.exists()) {
                            Log.e("Firebase", "Error: documento no encontrado ");
                        }else{
                            ArrayList<String> data = (ArrayList<String>) snapshot.get("cubos");
                            for (int i = 0; i < data.size(); i++){
                                cubos.add(data.get(i));
                            }
                        }
                    }
                });
    }

    // Se añade el cubo en los datos internos
    public void inicializacion(){

        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("cubos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (int i = 0; i < cubos.size(); i++ ){
                                    if (document.getId().equals(cubos.get(i))){

                                        nombres.add(document.getData().get("nombre").toString());
                                        carton.add(document.getData().get("carton").toString());
                                        vidrio.add(document.getData().get("vidrio").toString());
                                        plastico.add(document.getData().get("plastico").toString());
                                        organico.add(document.getData().get("organico").toString());

                                    }
                                }
                            }
                            items = nombres.size();
                        }
                    }
                });
    }

    // Se imprime el layout
    @Override
    public int getItemViewType(final int position) {

        return R.layout.cubos_lista;

    }

    // Se crea el holder
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view);
    }

    // Se llama cuando se añade un cubo al recyclerView
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();
        db.collection("cubos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Si el usuario tiene cubos, tomamos nombres como referencia
                            if(nombres.size() > 0 && nombres != null) {
                                // Asignamos las variables a la vista del cubo
                                holder.getNombreCubo().setText(nombres.get(itemList));
                                itemList++;
                            }
                        }
                    }
                });
    }

    // Se determinan cuantos cubos se imprimen
    @Override
    public int getItemCount() {

        return items;
    }
}
