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

    public AdaptadorCubos(ArrayList<String> nombres, ArrayList<String> carton, ArrayList<String> vidrio, ArrayList<String> plastico, ArrayList<String> organico, ArrayList<String> cubos, int items, int itemList) {
        this.nombres = nombres;
        this.carton = carton;
        this.vidrio = vidrio;
        this.plastico = plastico;
        this.organico = organico;
        this.cubos = cubos;
        this.items = items;
        this.itemList = itemList;
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
