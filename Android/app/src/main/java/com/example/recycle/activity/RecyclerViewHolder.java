package com.example.recycle.activity;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView nombreCubo;
    private BarChart plasticChart;
    private BarChart backChart;
    private LineChart lineChart;
    private Button eliminarBoton;
    private Button editarBoton;
    private ImageView moreButton;
    private String cuboID;
    private FrameLayout opciones;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        nombreCubo = itemView.findViewById(R.id.nombreCubo);
        plasticChart = itemView.findViewById(R.id.bar_chart);
        backChart = itemView.findViewById(R.id.bar_back);
        lineChart = itemView.findViewById(R.id.line_chart);
        editarBoton = itemView.findViewById(R.id.btn_editarCubo);
        eliminarBoton = itemView.findViewById(R.id.btn_eliminarCubo);
        moreButton = itemView.findViewById(R.id.btn_more);
        opciones = itemView.findViewById(R.id.tarjetaMore);
    }

    // Devuelve si es un cubo o un boton en base a si tiene nombreCubo o no
    public int getHolderType() {
        if(getNombreCubo() != null) {
            return 1; // Cubo
        } else {
            return 2; // Boton
        }
    }

    // Devuelve la vista
    public TextView getNombreCubo(){
        return nombreCubo;
    }

    public BarChart getPlasticChart() {
        return plasticChart;
    }
    public LineChart getLineChart() {
        return lineChart;
    }

    public String getCuboID() {
        return cuboID;
    }

    public void setCuboID(String cuboID) {
        this.cuboID = cuboID;
    }

    public Button getEliminarBoton() {
        return eliminarBoton;
    }

    public Button getEditarBoton() {
        return editarBoton;
    }

    public ImageView getMoreButton() {
        return moreButton;
    }

    public FrameLayout getOpciones() {
        return opciones;
    }

    public BarChart getBackChart() {
        return backChart;
    }
}