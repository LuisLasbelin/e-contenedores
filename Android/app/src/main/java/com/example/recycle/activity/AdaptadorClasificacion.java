package com.example.recycle.activity;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Clasificacion;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdaptadorClasificacion extends RecyclerView.Adapter<AdaptadorClasificacion.ViewHolderClasificacion>{

    List<Clasificacion> clasificacion;

    public AdaptadorClasificacion(List<Clasificacion> clasificacion) {
        this.clasificacion = clasificacion;
    }

    @NonNull
    @Override
    public ViewHolderClasificacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clasificacion_lista, parent, false);
        return new AdaptadorClasificacion.ViewHolderClasificacion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClasificacion holder, int position) {
        holder.posicion.setText(String.valueOf(position + 1));
        if (position + 1 == 1) {
            holder.posicion.setBackgroundResource(R.drawable.transparentbox1);
        }
        if (position + 1 == 2) {
            holder.posicion.setBackgroundResource(R.drawable.transparentbox2);
        }
        if(position + 1 == 3) {
            holder.posicion.setBackgroundResource(R.drawable.transparentbox4);
        }
        holder.nombre.setText(clasificacion.get(position).getNombre());
        holder.puntuacion.setText(String.valueOf(clasificacion.get(position).getPuntuacion()));

        Log.d("Clasificacion", String.valueOf(position) + clasificacion.get(position).getNombre());
        if (clasificacion.get(position).getNombre().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            holder.constraintLayout.setBackgroundResource(R.drawable.transparentbox3);
            holder.nombre.setTypeface(null, Typeface.BOLD);
            holder.puntuacion.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return clasificacion.size();
    }

    public class ViewHolderClasificacion extends RecyclerView.ViewHolder {
        private TextView nombre, posicion, puntuacion;
        private ConstraintLayout constraintLayout;

        public ViewHolderClasificacion(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nombreClasificacion);
            posicion = (TextView) itemView.findViewById(R.id.numeroClasificacion);
            puntuacion = (TextView) itemView.findViewById(R.id.puntuacion);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.clasificacionLayout);
        }

        public TextView getPosicion() {
            return posicion;
        }
    }
}
