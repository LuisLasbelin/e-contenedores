package com.example.recycle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycle.R;
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

public class AdaptadorCubos extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<String> nombres = new ArrayList<String>();
    private ArrayList<String> carton = new ArrayList<String>();
    private ArrayList<String> vidrio = new ArrayList<String>();
    private ArrayList<String> plastico = new ArrayList<String>();
    private ArrayList<String> organico = new ArrayList<String>();
    private ArrayList<String> cubos = new ArrayList<String>();
    private ArrayList<String> timestamp = new ArrayList<String>();
    private String TAG = "cubos";
    private int items = 0;
    private int itemList = 0;

    // Grafica temporal
    List<Entry> plasticoList = new ArrayList<>();
    List<Entry> vidrioList = new ArrayList<>();
    List<Entry> organicoList = new ArrayList<>();
    List<Entry> cartonList = new ArrayList<>();

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    Activity activity = null;
    Context context = null;

    public AdaptadorCubos(ArrayList<String> nombres,ArrayList<String> timestamp, ArrayList<String> carton, ArrayList<String> vidrio, ArrayList<String> plastico, ArrayList<String> organico, ArrayList<String> cubos, int items, int itemList, Activity activity) {
        this.nombres = nombres;
        this.carton = carton;
        this.vidrio = vidrio;
        this.plastico = plastico;
        this.organico = organico;
        this.cubos = cubos;
        this.items = items;
        this.itemList = itemList;
        this.timestamp = timestamp;
        this.activity = activity;
        this.context = activity.getBaseContext();
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
                                if(holder.getHolderType() == 1) {
                                    // Modificamos el cubo para mostrar los datos que queremos
                                    // Nombre
                                    holder.getNombreCubo().setText(nombres.get(itemList));
                                    // Gráfica plástico
                                    List<BarEntry> barMeasures = new ArrayList<>();
                                    // Position and value of the bar
                                    barMeasures.add(new BarEntry(0f, Integer.parseInt(plastico.get(itemList))));
                                    barMeasures.add(new BarEntry(1f, Integer.parseInt(carton.get(itemList))));
                                    barMeasures.add(new BarEntry(2f, Integer.parseInt(vidrio.get(itemList))));
                                    barMeasures.add(new BarEntry(3f, Integer.parseInt(organico.get(itemList))));
                                    // Set the data
                                    BarDataSet set = new BarDataSet(barMeasures, "Medidas");
                                    // Propiedades de las barras
                                    set.setValueTextSize(0f);
                                    set.setHighlightEnabled(false);
                                    // Ponemos los colores del set de datos
                                    set.setColors(ContextCompat.getColor(context, R.color.colorBox1),
                                            ContextCompat.getColor(context, R.color.colorBox2),
                                            ContextCompat.getColor(context, R.color.colorBox3),
                                            ContextCompat.getColor(context, R.color.colorBox4));
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

                                    // Ponemos el dato de plástico
                                    plasticoList.add(new Entry(Float.parseFloat(timestamp.get(itemList)), Float.parseFloat(plastico.get(itemList))));
                                    vidrioList.add(new Entry(Float.parseFloat(timestamp.get(itemList)), Float.parseFloat(vidrio.get(itemList))));
                                    organicoList.add(new Entry(Float.parseFloat(timestamp.get(itemList)), Float.parseFloat(organico.get(itemList))));
                                    cartonList.add(new Entry(Float.parseFloat(timestamp.get(itemList)), Float.parseFloat(carton.get(itemList))));
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
                                    holder.getLineChart().getXAxis().setDrawLabels(true);
                                    holder.getLineChart().setDoubleTapToZoomEnabled(false);
                                    holder.getLineChart().setPinchZoom(false);
                                    holder.getLineChart().setScaleEnabled(false);
                                    holder.getLineChart().getXAxis().setDrawLabels(false);
                                    holder.getLineChart().invalidate(); // refresh

                                    // Assign ID var
                                    holder.setCuboID(cubos.get(itemList));
                                    Log.e("CUBOS", holder.getCuboID());

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
