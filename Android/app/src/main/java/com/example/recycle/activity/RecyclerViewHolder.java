package com.example.recycle.activity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView nombreCubo;
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        nombreCubo = itemView.findViewById(R.id.nombreCubo);
    }

    // Devuelve si es un cubo o un boton en base a si tiene nombreCubo o no
    public String getHolderType() {
        if(getNombreCubo() != null) {
            return "cubo";
        } else {
            return "boton";
        }
    }

    // Devuelve la vista
    public TextView getNombreCubo(){
        return nombreCubo;
    }
}