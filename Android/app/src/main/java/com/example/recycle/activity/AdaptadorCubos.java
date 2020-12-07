package com.example.recycle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
import com.example.recycle.model.Cubo;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class AdaptadorCubos extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Cubo> cubos = new ArrayList<>();
    private String TAG = "cubos";
    private int items = 0;
    private int itemList = 0;

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    Activity activity = null;
    Context context = null;

    public AdaptadorCubos(List<Cubo> cubos, int items, int itemList, Activity activity) {
        this.cubos = cubos;
        this.items = items+1;
        this.itemList = itemList;
        this.activity = activity;
        this.context = activity.getBaseContext();
    }

    // Se crea el holder "items" veces
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        // Si no se ha completado la lista, se añade otro cubo
        if((itemList < items-1)) {
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

        // Si el usuario tiene cubos, tomamos nombres como referencia
        if(cubos.size() > 0) {
            // Asignamos las variables a la vista del cubo
            if(holder.getHolderType() == 1) {

                Cubo currCubo = cubos.get(itemList);
                // Assign ID var
                holder.setCuboID(currCubo.getID());
                Log.e("CUBOS", holder.getCuboID());

                List<Map<String, Object>> medidas = currCubo.getMedidas();

                // Modificamos el cubo para mostrar los datos que queremos
                // Nombre
                holder.getNombreCubo().setText(cubos.get(itemList).getNombre());
                // Gráfica plástico
                List<BarEntry> barMeasures = new ArrayList<>();

                // Busca la ultima medida tomada
                long reciente = 0;
                // Se busca cada dato dentro de cada medida
                for (Map<String, Object> medida: medidas) {
                    for (String item : medida.keySet()) {
                        // item es el nombre de la medida, es decir la fecha
                        if(Long.parseLong((String) item) > reciente) {
                            reciente = Long.parseLong((String) item);
                        }

                    }
                }

                // Se busca cada dato dentro de cada medida
                for (Map<String, Object> medida: medidas) {
                    for (String item: medida.keySet()) {
                        Map<String, Object> values = (Map<String, Object>) medida.get(item);
                        long fecha = Long.parseLong((String) item);
                        if(fecha == reciente) {
                            for (String medidaInterna : values.keySet()) {
                                switch (medidaInterna) {
                                    case "organico":
                                        barMeasures.add(new BarEntry(0f, parseInt((String) values.get(medidaInterna))));
                                        break;
                                    case "carton":
                                        barMeasures.add(new BarEntry(1f, parseInt((String) values.get(medidaInterna))));
                                        break;
                                    case "vidrio":
                                        barMeasures.add(new BarEntry(2f, parseInt((String) values.get(medidaInterna))));
                                        break;
                                    case "plastico":
                                        barMeasures.add(new BarEntry(3f, parseInt((String) values.get(medidaInterna))));
                                        break;
                                }
                            }
                        }
                    }
                }
                // Set the data
                BarDataSet set = new BarDataSet(barMeasures, "Medidas");
                // Propiedades de las barras
                set.setValueTextSize(0f);
                set.setHighlightEnabled(false);
                // Ponemos los colores del set de datos
                set.setColors(ContextCompat.getColor(context, R.color.colorBox4),
                        ContextCompat.getColor(context, R.color.colorBox2),
                        ContextCompat.getColor(context, R.color.colorBox3),
                        ContextCompat.getColor(context, R.color.colorBox1));
                // asignamos el set a los datos
                BarData data = new BarData(set);
                // Ajustes de estilo
                holder.getPlasticChart().setDescription(null);
                holder.getPlasticChart().setDrawGridBackground(false);
                holder.getPlasticChart().getXAxis().setEnabled(false);
                holder.getPlasticChart().getXAxis().setDrawLabels(false);
                holder.getPlasticChart().getAxisLeft().setEnabled(false);
                holder.getPlasticChart().getAxisLeft().setAxisMaximum(100);
                holder.getPlasticChart().getAxisLeft().setAxisMinimum(0);
                holder.getPlasticChart().getAxisRight().setEnabled(false);
                holder.getPlasticChart().getLegend().setEnabled(false);
                holder.getPlasticChart().setDoubleTapToZoomEnabled(false);
                holder.getPlasticChart().setPinchZoom(false);
                holder.getPlasticChart().setScaleEnabled(false);
                // Poner datos
                holder.getPlasticChart().setData(data);
                holder.getPlasticChart().invalidate(); // refresh


                // Grafica temporal
                List<Entry> plasticoList = new ArrayList<>();
                List<Entry> vidrioList = new ArrayList<>();
                List<Entry> organicoList = new ArrayList<>();
                List<Entry> cartonList = new ArrayList<>();
                // Se busca cada dato dentro de cada medida
                for (Map<String, Object> medida: medidas) {
                    for (String item: medida.keySet()) {
                        Map<String, Object> values = (Map<String, Object>) medida.get(item);
                        for (String medidaInterna : values.keySet()) {
                            switch (medidaInterna) {
                                case "organico":
                                    organicoList.add(new Entry(Float.parseFloat((String) item), Float.parseFloat((String) values.get(medidaInterna))));
                                    break;
                                case "carton":
                                    cartonList.add(new Entry(Float.parseFloat((String) item), Float.parseFloat((String) values.get(medidaInterna))));
                                    break;
                                case "vidrio":
                                    vidrioList.add(new Entry(Float.parseFloat((String) item), Float.parseFloat((String) values.get(medidaInterna))));
                                    break;
                                case "plastico":
                                    plasticoList.add(new Entry(Float.parseFloat((String) item), Float.parseFloat((String) values.get(medidaInterna))));
                                    break;
                            }
                        }
                    }
                }

                LineDataSet setPlastico = new LineDataSet(plasticoList, "Plastico");
                LineDataSet setVidrio = new LineDataSet(vidrioList, "Vidrio");
                LineDataSet setOrganico = new LineDataSet(organicoList, "Organico");
                LineDataSet setCarton = new LineDataSet(cartonList, "Carton");
                setPlastico.setAxisDependency(YAxis.AxisDependency.LEFT);
                setVidrio.setAxisDependency(YAxis.AxisDependency.LEFT);
                setOrganico.setAxisDependency(YAxis.AxisDependency.LEFT);
                setCarton.setAxisDependency(YAxis.AxisDependency.LEFT);
                setPlastico.setValueTextSize(0f);
                setCarton.setValueTextSize(0f);
                setOrganico.setValueTextSize(0f);
                setVidrio.setValueTextSize(0f);

                setPlastico.setColor(ContextCompat.getColor(context, R.color.colorBox1));
                setCarton.setColor(ContextCompat.getColor(context, R.color.colorBox2));
                setVidrio.setColor(ContextCompat.getColor(context, R.color.colorBox3));
                setOrganico.setColor(ContextCompat.getColor(context, R.color.colorBox4));
                setPlastico.setCircleColor(ContextCompat.getColor(context, R.color.colorBox1));
                setCarton.setCircleColor(ContextCompat.getColor(context, R.color.colorBox2));
                setVidrio.setCircleColor(ContextCompat.getColor(context, R.color.colorBox3));
                setOrganico.setCircleColor(ContextCompat.getColor(context, R.color.colorBox4));
                // Establecemos los datos en la gráfica de lineas
                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(setVidrio);
                dataSets.add(setPlastico);
                dataSets.add(setOrganico);
                dataSets.add(setCarton);
                LineData linearData = new LineData(dataSets);
                holder.getLineChart().setData(linearData);
                holder.getLineChart().getAxisRight().setEnabled(false);
                holder.getLineChart().getLegend().setEnabled(false);
                holder.getLineChart().setDescription(null);
                holder.getLineChart().setDrawGridBackground(false);
                holder.getLineChart().setDoubleTapToZoomEnabled(false);
                holder.getLineChart().setPinchZoom(false);
                holder.getLineChart().setScaleEnabled(false);
                holder.getLineChart().getXAxis().setDrawLabels(false);
                holder.getLineChart().invalidate(); // refresh

                // Asignamos el listener al eliminar cubo
                holder.getEditarBoton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ActividadConfirmarEditar.class);
                        i.putExtra("cuboID", holder.getCuboID());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(i);
                        activity.finish();
                    }
                });

                // Asignamos el listener al eliminar cubo
                holder.getEliminarBoton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, ActividadConfirmarBorrar.class);
                        i.putExtra("cuboID", holder.getCuboID());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                });

                // More button
                holder.getMoreButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FrameLayout opciones = holder.getOpciones();
                        if(opciones.getVisibility() == View.GONE){
                            opciones.setVisibility(View.VISIBLE);
                        }else{
                            opciones.setVisibility(View.GONE);
                        }
                    }
                });
            }
            itemList++;
            // Se añade el boton de anyadir al final de la lista
            if(itemList == items) {
                Log.e(TAG, "Todos los cubos puestos");
            }
        }
    }


    // Se determinan cuantos cubos se imprimen
    @Override
    public int getItemCount() {
        return items;
    }

}
