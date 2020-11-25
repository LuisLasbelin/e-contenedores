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
    private int items = 0;
    private int itemList = 0;

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    public AdaptadorCubos(ArrayList<String> data) {

        inicializarUsuario();
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
                            if(nombres.size() != 0) {
                                items = nombres.size();
                            } else{
                                Log.e(TAG, "Todos los cubos puestos");
                                // El recycler view añade un nuevo item cuando
                                // items > la cantidad de items actuales
                                items = 1;
                                itemList++;
                            }
                        }
                    }
                });
    }

    // Se crea el holder "items" veces
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        // Si no se ha completado la lista, se añade otro cubo
        if((itemList != items)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cubos_lista, parent, false);
        } else {
            // Si se ha completado la lista de cubos, se añade el añadir
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boton_anyadir, parent, false);
        }

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
                            if(nombres.size() > 0) {
                                // Asignamos las variables a la vista del cubo
                                if(holder.getHolderType().equals("cubo")) {
                                    holder.getNombreCubo().setText(nombres.get(itemList));
                                }
                                itemList++;
                                // Se añade el boton de anyadir al final de la lista
                                if(itemList == items) {
                                    Log.e(TAG, "Todos los cubos puestos");
                                    // El recycler view añade un nuevo item cuando
                                    // items > la cantidad de items actuales
                                    items++;
                                    itemList++;
                                }
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
