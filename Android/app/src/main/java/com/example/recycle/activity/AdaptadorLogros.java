package com.example.recycle.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Logro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorLogros extends RecyclerView.Adapter<AdaptadorLogros.ViewHolderLogros> {

    List<Logro> logros = new ArrayList<>();
    private int contadorLogros = 0;

    // Notificaciones
    static final String CANAL_ID = "logros";
    static final int NOTIFICACION_ID = 2;
    private boolean notificacionEnviada = false;

    public AdaptadorLogros(List<Logro> logros) {
        this.logros = logros;
    }

    @NonNull
    @Override
    public ViewHolderLogros onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logro_lista, parent, false);
        return new ViewHolderLogros(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderLogros holder, int position) {
        String progreso;

        holder.nombre.setText(logros.get(position).getNombre());
        holder.descripcion.setText(logros.get(position).getDescripcion());
        progreso = logros.get(position).getProgreso() + "/" + logros.get(position).getCheck();
        holder.progreso.setText(progreso);
        if (logros.get(position).getProgreso() >= logros.get(position).getCheck()) {
            holder.hecho.setVisibility(View.VISIBLE);
            progreso = logros.get(position).getCheck() + "/" + logros.get(position).getCheck();
            holder.progreso.setText(progreso);
            contadorLogros++;
        }

    }

    @Override
    public int getItemCount() {
        return logros.size();
    }

    public class ViewHolderLogros extends RecyclerView.ViewHolder {
        private TextView nombre, descripcion, progreso;
        private ImageView hecho;

        public ViewHolderLogros(@NonNull View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.nombreLogro);
            descripcion = (TextView) itemView.findViewById(R.id.descripcionLogro);
            progreso = (TextView) itemView.findViewById(R.id.progresoLogro);
            hecho = (ImageView) itemView.findViewById(R.id.logroCumplido);
        }
    }
}
