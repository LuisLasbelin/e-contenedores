package com.example.recycle.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.example.recycle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.recycle.activity.ActividadConfirmarEditar.RESULTADO_FOTO;
import static com.example.recycle.activity.ServicioLogros.mandarNotificacion;

public class ForosActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mapa;
    private final LatLng UPV = new LatLng(39.481106, -0.340987);
    //Foto
    public Button añadirFoto;
    Activity activity = null;
    private StorageReference storageRef;
    public String nombreDeLaFoto;
    //Ubicacion
    private GpsTracker gpsTracker;
    public String lat;
    public String lon;
    private File file;
    double latitude;
    double longitude;
    public double latitudeIni;
    public double longitudeIni;
    //Tarjeta de info
    TextView markertxt;
    //==========================================================================================
    //Definimos firestore
    //==========================================================================================
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> datos = new HashMap<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foros);
        //==========================================================================================
        //Creamos el mapa
        //==========================================================================================
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        //==========================================================================================
        //Fotos
        activity = this;
        añadirFoto = findViewById(R.id.btn_tomarFoto);
        storageRef = FirebaseStorage.getInstance().getReference();
        //==========================================================================================
        //Establecemos el layout y el boton por defecto
        Button btnM = (Button) findViewById(R.id.btn_tomarFoto);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.tarjetainfo);
        layout.setVisibility(View.GONE);
        btnM.setVisibility(View.VISIBLE);

    }

    @Override public void onMapReady(GoogleMap googleMap) {
        //==========================================================================================
        //Tomamos la ubicacion
        //==========================================================================================
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        gpsTracker = new GpsTracker(ForosActivity.this);
        if(gpsTracker.canGetLocation()){
            latitudeIni = gpsTracker.getLatitude();
            longitudeIni = gpsTracker.getLongitude();
        }else{
            gpsTracker.showSettingsAlert();
        }

        LatLng ACTU = new LatLng(latitudeIni,longitudeIni);
        //Cuando el mapa este listo
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ACTU, 15));
        mapa.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
        descargarTodosLosDatos();
    }

    //==============================================================================================
    //Foto con ubicacion adjunta
    //==============================================================================================

    public void hacerFoto(View view) {

        //==========================================================================================
        //Tomamos la ubicacion
        //==========================================================================================

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        gpsTracker = new GpsTracker(ForosActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            lat = String.valueOf(latitude);
            lon = String.valueOf(longitude);
            Log.e("longitude",lon);
        }else{
            gpsTracker.showSettingsAlert();
        }

        //==========================================================================================
        //Meter ubicacion
        //==========================================================================================

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                file = File.createTempFile(lat + "," + lon, ".jpg" ,
                        activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                if (Build.VERSION.SDK_INT >= 24) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                            activity, "team1.1.recycle.fileProvider", file));
                } else {
                    intent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                }
                activity.startActivityForResult(intent, RESULTADO_FOTO);
            } catch (IOException ex) {
                Toast.makeText(activity, "Error al crear fichero de imagen",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        nombreDeLaFoto = file.getName();
        //==========================================================================================
        //Definimos Storage
        //==========================================================================================
        StorageReference ficheroRef = storageRef.child("foro/"+ file.getName());
        ficheroRef.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Log.d("Almacenamiento", "Fichero subido");
                        LatLng ACTU = new LatLng(latitude,longitude);
                        String la = String.valueOf(latitude);
                        String lo = String.valueOf(longitude);
                        mapa.addMarker(new MarkerOptions().position(ACTU)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .title(nombreDeLaFoto)
                                .snippet("Residuo"));

                        //==================================================================================
                        //Subimos a firestore la foto
                        //==================================================================================
                        datos.put("lat", lat);
                        datos.put("long", lon);
                        datos.put("nombre",nombreDeLaFoto);
                        db.collection("foros").document(nombreDeLaFoto).set(datos);

                        //==========================================================================================
                        //OnClick en el marcador
                        //==========================================================================================
                        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                //Al pulsar el boton mostramos la tarjeta y eliminamos el boton para hacer fotos
                                Button btnM = (Button) findViewById(R.id.btn_tomarFoto);
                                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.tarjetainfo);
                                ConstraintLayout layoutPantallaCargaFoto = (ConstraintLayout) findViewById(R.id.pantallaCargaFoto);
                                layout.setVisibility(View.VISIBLE);
                                btnM.setVisibility(View.GONE);
                                //Ponemos una pequeña pantalla de carga para que la foto no aparezca de repente
                                new CountDownTimer(1000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        layoutPantallaCargaFoto.setVisibility(View.VISIBLE);
                                    }

                                    public void onFinish() {
                                        layoutPantallaCargaFoto.setVisibility(View.GONE);
                                    }
                                }.start();
                                //Luego tomamos el nombre del marcador para mostrarlo en la tarjeta
                                markertxt = findViewById(R.id.marker);
                                String markertitle = marker.getTitle();
                                String title = markertitle ;
                                markertxt.setText(title);
                                bajarFichero(title);
                                return false;
                            }
                        });
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("logros").document("Ciudadano ejemplar")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> datos = new HashMap<>();
                                datos.put("progreso", Integer.parseInt(task.getResult().get("progreso").toString()) + 1);
                                mandarNotificacion(activity, getApplicationContext(),Integer.parseInt(task.getResult().get("progreso").toString()) + 1, 5, "Ciudadano ejemplar");
                                db.collection("usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("logros").document("Ciudadano ejemplar")
                                        .update(datos);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Log.e("Almacenamiento", "ERROR: subiendo fichero");
                    }
                });
    }
    //==============================================================================================
    //Funcion onClick en el map
    //==============================================================================================
    @Override public void onMapClick(LatLng puntoPulsado) {
        Button btnM = (Button) findViewById(R.id.btn_tomarFoto);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.tarjetainfo);
        ConstraintLayout layoutPantallaCargaFoto = (ConstraintLayout) findViewById(R.id.pantallaCargaFoto);
        layout.setVisibility(View.GONE);
        layoutPantallaCargaFoto.setVisibility(View.GONE);
        btnM.setVisibility(View.VISIBLE);
    }
    //==========================================================================================
    //Funcion bajar foto para la tarjeta
    //==========================================================================================
    private void bajarFichero(String nombre) {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("foro/"+ nombre);
        ficheroRef.getFile(localFile)
                .addOnSuccessListener(new
                                              OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                                                  @Override
                                                  public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                                                      Log.d("Almacenamiento", "Fichero bajado");
                                                      ImageView imageView = findViewById(R.id.imageView);
                                                      imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                                                  }
                                              }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });
    }

    public void bajarFicherosCreados(String nombre){
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageRef.child("foro/"+ nombre);
        ficheroRef.getFile(localFile)
                .addOnSuccessListener(new
                                              OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                                                  @Override
                                                  public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                                                      Log.d("Almacenamiento", "Fichero bajado");
                                                      ImageView imageView = findViewById(R.id.imageView);
                                                      imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                                                  }
                                              }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });
    }

    public void descargarTodosLosDatos(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> datos = new HashMap<>();

        //==========================================================================================
        //Tomo datos del firebase
        //==========================================================================================
        //final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("foros")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String nombreMarkerS;
                            String nombreMarker;
                            LatLng posMarker;
                            String latiMarkerString;
                            String longMarkerString;
                            String[] parts;
                            double latiMarker;
                            double longMarker;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.e("M", document.getId() + " => " + document.getData());

                                nombreMarkerS = document.getString("nombre");

                                nombreMarker = nombreMarkerS.replace(".jpg","");

                                parts = nombreMarker.split(",");

                                latiMarkerString = parts[0];

                                longMarkerString = parts[1];

                                latiMarker = Double.parseDouble(latiMarkerString);

                                longMarker = Double.parseDouble(longMarkerString);

                                posMarker = new LatLng(latiMarker,longMarker);

                                //Log.e("M", nombreUsuario);

                                mapa.addMarker(new MarkerOptions().position(posMarker)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                        .title(nombreMarkerS)
                                        .snippet("Residuo"));

                            }

                        } else {
                            Log.d("M", "Error getting documents: ", task.getException());
                        }
                    }
                });

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Al pulsar el boton mostramos la tarjeta y eliminamos el boton para hacer fotos
                Button btnM = (Button) findViewById(R.id.btn_tomarFoto);
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.tarjetainfo);
                ConstraintLayout layoutPantallaCargaFoto = (ConstraintLayout) findViewById(R.id.pantallaCargaFoto);

                layout.setVisibility(View.VISIBLE);
                btnM.setVisibility(View.GONE);

                //Ponemos una pequeña pantalla de carga para que la foto no aparezca de repente
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        layoutPantallaCargaFoto.setVisibility(View.VISIBLE);
                    }

                    public void onFinish() {
                        layoutPantallaCargaFoto.setVisibility(View.GONE);
                    }
                }.start();
                //Luego tomamos el nombre del marcador para mostrarlo en la tarjeta
                markertxt = findViewById(R.id.marker);
                String markertitle = marker.getTitle();
                String title = markertitle ;
                markertxt.setText(title);
                bajarFicherosCreados(title);
                return false;
            }
        });
    }


}