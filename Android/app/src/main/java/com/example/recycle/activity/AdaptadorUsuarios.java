package com.example.recycle.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorUsuarios extends
        RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {
    private LayoutInflater inflador;

    List<String> lista1 = new ArrayList<>();
    List<String> lista2 = new ArrayList<>();
    List<String> lista3 = new ArrayList<>();

    public AdaptadorUsuarios(Context context,   List<String> lista1, List<String> lista2, List<String> lista3) {
        this.lista1 = lista1;
        this.lista2 = lista2;
        this.lista3 = lista3;
        inflador =(LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.usuarios_lista, parent, false);
        return new ViewHolder(v);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.titulo.setText(lista1.get(i));
        holder.subtitutlo.setText(lista2.get(i));
        holder.subtitulo2.setText(lista3.get(i));

    }
    @Override
    public int getItemCount() {
        return lista1.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, subtitutlo, subtitulo2;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            subtitutlo = itemView.findViewById(R.id.subtitulo);
            subtitulo2 = itemView.findViewById(R.id.subtitulo2);
        }
    }
}