package com.example.recycle.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static com.example.recycle.activity.ActividadConfirmarEditar.RESULTADO_FOTO;

public class ForosActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private GoogleMap mapa;

    private final LatLng UPV = new LatLng(39.481106, -0.340987);
    //Foto
    public Button añadirFoto;
    Activity activity = null;
    private StorageReference storageRef;
    //Ubicacion
    private GpsTracker gpsTracker;
    public String lat;
    public String lon;
    private File file;
    public double latitudeIni;
    public double longitudeIni;

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
        //mapa.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }

    //==============================================================================================
    //Utilidades
    //==============================================================================================
    /*
    public void moveCamera(View view) {
        mapa.moveCamera(CameraUpdateFactory.newLatLng(UPV));
    }
    public void animateCamera(View view) {
        mapa.animateCamera(CameraUpdateFactory.newLatLng(UPV));
    }
    public void addMarker(View view) {
        mapa.addMarker(new MarkerOptions().position(mapa.getCameraPosition().target));
    }
    @Override public void onMapClick(LatLng puntoPulsado) {
        mapa.addMarker(new MarkerOptions().position(puntoPulsado)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }

     */

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
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            lat = String.valueOf(latitude);
            lon = String.valueOf(longitude);

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

        StorageReference ficheroRef = storageRef.child("foro/"+ file.getName());
        ficheroRef.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Log.d("Almacenamiento", "Fichero subido");
                        LatLng ACTU = new LatLng(latitudeIni,longitudeIni);
                        String la = String.valueOf(latitudeIni);
                        String lo = String.valueOf(longitudeIni);
                        mapa.addMarker(new MarkerOptions().position(ACTU)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .title(la + " , " + lo)
                                .snippet("Residuo"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Log.e("Almacenamiento", "ERROR: subiendo fichero");
                    }
                });
    }
}