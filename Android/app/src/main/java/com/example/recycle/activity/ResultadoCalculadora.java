package com.example.recycle.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycle.R;

public class ResultadoCalculadora extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculadora_resultados);
        Bundle extras = getIntent().getExtras();
        Double kilovatios = extras.getDouble("Kilovatios");
        Double kmAutobus = extras.getDouble("KmAutobus");
        Double kmCoche = extras.getDouble("KmCoche");
        Double vuelos = extras.getDouble("Vuelos");
        Double dieta = extras.getDouble("Dieta");
        Double total = extras.getDouble("Total");

        // Instanciamos el boton de cancelar y aceptar
        Button continuar = findViewById(R.id.continuar);
        TextView kilovatiosTexto = findViewById(R.id.kilovatios);
        TextView cocheTexto = findViewById(R.id.coche);
        TextView autobusTexto = findViewById(R.id.autobus);
        TextView vueloTexto = findViewById(R.id.vuelo);
        TextView dietaTexto = findViewById(R.id.dieta);
        TextView resultado = findViewById(R.id.resultado);

        // Si es mayor a 0.06 el consumo es superior a la media española
        if (kilovatios > 0.06){
            for (Drawable drawable : kilovatiosTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // El consumo esta en la media
        else if (kilovatios < 0.06 && kilovatios > 0.03){
            for (Drawable drawable : kilovatiosTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(252,186,3), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // Si es menor a 0.05 el consumo es inferior a la media española
        else if (kilovatios < 0.03){
            for (Drawable drawable : kilovatiosTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        // Multiplicamos el factorCoche, que representa el tipo de combustible, por la media de km realizados al mes por españoles 1100
        // Si es mayor el consumo es superior a la media española
        if (kmCoche > 1100*CalculadoraDeCarbonoActivity.factorCoche){
            for (Drawable drawable : cocheTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // El consumo esta en la media
        else if (kmCoche < 1100*CalculadoraDeCarbonoActivity.factorCoche && kmCoche > 800*CalculadoraDeCarbonoActivity.factorCoche){
            for (Drawable drawable : cocheTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(252,186,3), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // Si es menor el consumo es inferior a la media española
        else if (kmCoche < 800*CalculadoraDeCarbonoActivity.factorCoche){
            for (Drawable drawable : cocheTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        // La media de distancia de recorrido en autobus diario es de 6km, si contamos la ida y la vuelta 12km, multiplicamos por los dias laborales y
        // luego por el combustible, que tomaremos el diesel, el resultado es de 240 kilometros * 0.0001743
        // Si es mayor a 0.17 el consumo es superior a la media española
        if (kmAutobus > 0.041){
            for (Drawable drawable : autobusTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // El consumo esta en la media
        else if (kmAutobus < 0.041 && kmAutobus > 0.025){
            for (Drawable drawable : autobusTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(252,186,3), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // Si es menor a 0.7 el consumo es inferior a la media española
        else if (kmAutobus < 0.025){
            for (Drawable drawable : autobusTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        // Si es mayor a 0.39 el consumo es superior a la media española, equivale a dos vuelos
        if (vuelos > 0.38){
            for (Drawable drawable : vueloTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // El consumo esta en la media, equivale a un vuelo
        else if (vuelos <= 0.38 && vuelos >= 0.18){
            for (Drawable drawable : vueloTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(252,186,3), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // Si es menor a 0.4 el consumo es inferior a la media española
        else if (vuelos < 0.18){
            for (Drawable drawable : vueloTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        // Multiplicamos el factorDieta, que representa el tipo de dieta, por la media de euros gastados al mes por españoles 187
        // Si es mayor el consumo es superior a la media española
        if (dieta > 187 * CalculadoraDeCarbonoActivity.factorDieta){
            for (Drawable drawable : dietaTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // El consumo esta en la media
        else if (dieta < 187 * CalculadoraDeCarbonoActivity.factorDieta && vuelos > 136 * CalculadoraDeCarbonoActivity.factorDieta){
            for (Drawable drawable : dietaTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(252,186,3), PorterDuff.Mode.SRC_IN));
                }
            }
        }
        // Si es menor el consumo es inferior a la media española
        else if (dieta < 136 * CalculadoraDeCarbonoActivity.factorDieta){
            for (Drawable drawable : dietaTexto.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.rgb(40, 165, 37), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        // Si es mayor a 0.5 el consumo es superior a la media española
        if (total > 0.5){
            resultado.setText("Huella de carbono superior a la media");
        }
        // El consumo esta en la media
        else if (total < 0.5 && total > 0.35){
            resultado.setText("Huella de carbono sobre la media");
        }
        // Si es menor a 0.4 el consumo es inferior a la media española
        else if (total < 0.35){
            resultado.setText("Huella de carbono inferior a la media");
        }

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
