package com.example.recycle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycle.R;

public class CalculadoraDeCarbonoActivity extends AppCompatActivity {

    // Instanciamos todas las variables a utilizar
    int personasInt;
    double kiloVatiosDouble;
    double kmEnCocheDouble;
    double kmEnAutobusDouble;
    double vuelosRealizadosDouble;
    double dietaDouble;
    static double factorCoche;
    static double factorDieta;
    double total = 0;
    TextView ayuda;
    RadioButton gasolina;
    RadioButton diesel;
    RadioButton electrico;
    RadioButton hibrido;
    RadioButton todo;
    RadioButton pescado;
    RadioButton vegetariano;
    RadioButton vegano;
    Toast toast;
    public String TAG = "datosCalculadora";


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora);


        // Inicio: Instanciamos las secciones
        final FrameLayout opcionesElectricas = findViewById(R.id.tarjetaMoreElectrico);
        Button botonElectrico = findViewById(R.id.btn_moreElectrico);
        botonElectrico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(opcionesElectricas);
            }
        });

        final FrameLayout opcionesVehiculo = findViewById(R.id.tarjetaMoreVehiculo);
        Button botonVehiculo = findViewById(R.id.btn_moreVehiculo);
        botonVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(opcionesVehiculo);
            }
        });

        final FrameLayout opcionesTransporte = findViewById(R.id.tarjetaMoreVehiculoPublico);
        Button botonVehiculoPublico = findViewById(R.id.btn_moreVehiculoPublico);
        botonVehiculoPublico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(opcionesTransporte);
            }
        });

        final FrameLayout opcionesVuelo = findViewById(R.id.tarjetaMoreVuelos);
        Button botonVuelos = findViewById(R.id.btn_moreVuelos);
        botonVuelos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(opcionesVuelo);
            }
        });

        final FrameLayout opcionesComida = findViewById(R.id.tarjetaMoreComida);
        Button botonComida = findViewById(R.id.btn_moreComida);
        botonComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(opcionesComida);
            }
        });

        // Final: Instanciamos las secciones

        // Instanciamos el boton de ayuda
        ayuda = findViewById(R.id.ayuda);
        ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAyuda();
            }
        });

        // Instanciamos el boton de cancelar y aceptar
        Button cancelar = findViewById(R.id.cancelar);
        Button aceptar = findViewById(R.id.aceptar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptar();
            }
        });

        // Instanciamos los radio button
        gasolina = findViewById(R.id.gasolina);
        diesel = findViewById(R.id.diesel);
        electrico = findViewById(R.id.electrico);
        hibrido = findViewById(R.id.hibrido);
        todo = findViewById(R.id.todo);
        pescado = findViewById(R.id.piscivegetariano);
        vegetariano = findViewById(R.id.vegetariano);
        vegano = findViewById(R.id.vegano);
    }

    void cambiarVisibilidad(FrameLayout opciones){
        if(opciones.getVisibility() == View.GONE){
            opciones.setVisibility(View.VISIBLE);
        }else{
            opciones.setVisibility(View.GONE);
        }
    }

    void aceptar(){

        // Recogemos los datos
        EditText personas = findViewById(R.id.numeroPersonas);
        EditText kiloVatios = findViewById(R.id.kilovatiosHora);
        EditText kmEnCoche = findViewById(R.id.kmEnCoche);
        EditText kmEnAutobus = findViewById(R.id.kmEnAutobus);
        EditText vuelosRealizados = findViewById(R.id.vuelosRealizados);
        EditText dieta = findViewById(R.id.dieta);

        // Inicio: Verificamos si los datos introducidos son correctos, si no enviamos una alerta y detenemos el proceso

        try {
            personasInt = Integer.parseInt(personas.getText().toString());
            if (personasInt <= 0){
                Toast toast = Toast.makeText(this, "Numero de personas no valido", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de personas no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            kiloVatiosDouble = Integer.parseInt(kiloVatios.getText().toString());
            if (kiloVatiosDouble < 0){
                Toast toast = Toast.makeText(this, "Numero de kilovatios no valido", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de kilovatios no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            kmEnCocheDouble = Integer.parseInt(kmEnCoche.getText().toString());
            if (kmEnCocheDouble < 0){
                Toast toast = Toast.makeText(this, "Numero de horas en coche no valido", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de horas en coche no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            kmEnAutobusDouble = Integer.parseInt(kmEnAutobus.getText().toString());
            if (kmEnAutobusDouble < 0){
                Toast toast = Toast.makeText(this, "Numero de horas en autobus no validas", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de horas en autobus no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            vuelosRealizadosDouble = Integer.parseInt(vuelosRealizados.getText().toString());
            if (vuelosRealizadosDouble < 0){
                Toast toast = Toast.makeText(this, "Numero de vuelos realizados no valido", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de vuelos realizados no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            dietaDouble = Integer.parseInt(dieta.getText().toString());
            if (dietaDouble < 0){
                Toast toast = Toast.makeText(this, "Numero de dieta no valido", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Numero de dieta no valido", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Multiplicamos los kilovatios por el factor de emision de CO2 para la electricidad en España.
        kiloVatiosDouble = kiloVatiosDouble * 0.0002203;
        // Dividimos entre el numero de personas de la casa
        kiloVatiosDouble = kiloVatiosDouble/personasInt;

        if (gasolina.isChecked()){

            // Multiplicamos los kilometros por el factor de emision de CO2 de gasolina.
            kmEnCocheDouble = kmEnCocheDouble * 0.0001743;
            factorCoche = 0.0001743;

        }
        else if (diesel.isChecked()){

            // Multiplicamos los kilometros por el factor de emision de CO2 de diesel.
            kmEnCocheDouble = kmEnCocheDouble * 0.00016844;
            factorCoche = 0.00016844;

        }
        else if (electrico.isChecked()){

            // Multiplicamos los kilometros por el factor de emision de CO2 para la electricidad en España.
            kmEnCocheDouble = kmEnCocheDouble * 0.0002203;
            factorCoche = 0.0002203;

        }
        else if(hibrido.isChecked()){

            // Multiplicamos los kilovatios por el factor de emision de CO2.
            kmEnCocheDouble = kmEnCocheDouble * 0.00011558;
            factorCoche = 0.00011558;

        }

        kmEnAutobusDouble = kmEnAutobusDouble * 0.0001743;

        // Los vuelos de media distancia se consideran vuelos dentro de europa
        // Cantidad de vuelos por la emision de CO2 por kilometro por la media de kilometros de un vuelo medio
        vuelosRealizadosDouble = vuelosRealizadosDouble * 0.000195 * 1500;

        if (todo.isChecked()){

            // Multiplicamos los kilos por el factor de emision de CO2 de la carne.
            dietaDouble = dietaDouble * 0.002157;
            factorDieta = 0.002157;
        }
        else if (pescado.isChecked()){

            // Multiplicamos los kilos por el factor de emision de CO2 de la pescado.
            dietaDouble = dietaDouble * 0.001164;
            factorDieta = 0.001164;
        }
        else if (vegetariano.isChecked()){

            // Multiplicamos los kilos por el factor de emision de CO2 de los productos vegetarianos.
            dietaDouble = dietaDouble * 0.001143;
            factorDieta = 0.001143;
        }
        else if(vegano.isChecked()){

            // Multiplicamos los kilos por el factor de emision de CO2 de los productos veganos.
            dietaDouble = dietaDouble * 0.000867;
            factorDieta = 0.000867;
        }

        // Sumamos todas las emisiones
        total = kiloVatiosDouble + kmEnAutobusDouble + kmEnCocheDouble + vuelosRealizadosDouble + dietaDouble;

        Intent i = new Intent(this, ResultadoCalculadora.class);
        i.putExtra("Kilovatios" , kiloVatiosDouble);
        i.putExtra("KmAutobus" , kmEnAutobusDouble);
        i.putExtra("KmCoche" , kmEnCocheDouble);
        i.putExtra("Vuelos" , vuelosRealizadosDouble);
        i.putExtra("Dieta" , dietaDouble);
        i.putExtra("Total" , total);
        startActivity(i);
    }

    public void lanzarAyuda(){
        Intent i = new Intent(this, AyudaCalculadora.class);
        startActivity(i);
    }

}
