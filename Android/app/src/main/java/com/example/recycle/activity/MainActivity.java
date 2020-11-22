package com.example.recycle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback {
    // Nombres de las pestañas
    private String[] nombres = new String[]{"Mapa", "Inicio", "Opciones"};
    private int[] iconos = new int[]{R.drawable.ic_baseline_map_24, R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_settings_24};

    // MAPA
    private int locationRequestCode = 1000;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LatLng currentPosition;

    // Localizacion
    private LocationManager manejador;
    private String proveedor;

    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    // RecycleView
    private View vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firestore initialization
        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Nuevo usuario se le crea la lista de cubos vacía
        db.collection("usuarios").document(usuario.getEmail()).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e){
                        if (e != null) {
                            Log.e("Firebase", "Error al leer", e);
                        } else if (snapshot == null || !snapshot.exists()) {
                            Log.e("Firebase", "Error: documento no encontrado ");
                            Map<String, Object> datos = new HashMap<>();
                            // Añadimos una lista de cubos vacía y el mail del usuario
                            ArrayList<String> cubos = new ArrayList<>();
                            datos.put("cubos", cubos);
                            datos.put("mail", usuario.getEmail());
                            db.collection("usuarios").document(usuario.getEmail()).set(datos);
                        } else {
                            Log.d("Firestore", "datos:" + snapshot.getData());
                            }
                        }
                    });

        //Tabs
        //Pestañas
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(nombres[position]);
                        tab.setIcon(iconos[position]);
                    }
                }
        ).attach();

        viewPager.setCurrentItem(1, false);

    }

    public void onClickPerfil(View view) {

        Intent intent = new Intent(this, UsuarioActivity.class);
        startActivity(intent);

    }

    public void lanzarWeb(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://donhierro.com/es/"));
        startActivity(intent);
    }

    public void googleMaps(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:39.065614, -0.283374"));
        startActivity(intent);
    }

    public void mandarCorreo(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "asunto");
        intent.putExtra(Intent.EXTRA_TEXT, "texto del correo");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tostyfis360@gmail.com"});
        startActivity(intent);
    }

    // MAPA
    public void mapTabLoaded(SupportMapFragment supportMapFragment) {
        // MAPA
        // No es redundante! Se usa en la funcion onRequestPermissionsResult()
        mapFragment = (SupportMapFragment) supportMapFragment;

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            onConfirmPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onConfirmPermissions();
                } else {
                    Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // Cuando se confirman los permisos
    public void onConfirmPermissions() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(false);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Map options
            map.getUiSettings().setCompassEnabled(true);
            map.setMyLocationEnabled(true);

            // Si el usuario ya existe, cojemos sus datos y metemos su location en currentLocation
            // para añadir el marcador
            db.collection("usuarios").document(usuario.getEmail()).addSnapshotListener(
                    new EventListener<DocumentSnapshot>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e){
                            if (e != null) {
                                Log.e("Firebase", "Error al leer", e);
                            } else if (snapshot == null || !snapshot.exists()) {
                                Log.e("Firebase", "Error: documento no encontrado ");
                                // Si el usuario no existe, tomamos su posicion actual y la guardamos como default
                                manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
                                Criteria criterio = new Criteria();
                                criterio.setCostAllowed(false);
                                criterio.setAltitudeRequired(false);
                                criterio.setAccuracy(Criteria.ACCURACY_FINE);
                                proveedor = manejador.getBestProvider(criterio, true);
                                @SuppressLint("MissingPermission")
                                Location posicion = manejador.getLastKnownLocation(proveedor);

                                if(posicion != null) {
                                    Map<String, Object> datos = new HashMap<>();
                                    datos.put("mail", usuario.getEmail());
                                    datos.put("posicion", posicion);
                                    db.collection("usuarios").document(usuario.getEmail()).update(datos);
                                } else {
                                    Toast.makeText(getBaseContext(), R.string.gps_off, Toast.LENGTH_LONG).show();
                                }

                            } else {
                                // Fetch the data received
                                Map<String, Object> data = (Map<String, Object>) snapshot.get("posicion");
                                Object latitude = data.get("latitude");
                                Object longitude = data.get("longitude");
                                Log.d("Firestore", "datos:" + data);
                                currentPosition = new LatLng(Double.parseDouble(latitude.toString()), Double.parseDouble(longitude.toString()));

                                // Marcar posicion actual
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
                                map.addMarker(new MarkerOptions().position(currentPosition)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                        }
                    });
        }
    }

    // Abre el escáner de QR
    public void agregarCubo(View view) {
            new IntentIntegrator(this).initiateScan();
        }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cubos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    int length = document.getDocuments().size();
                    int i;

                    for (i = 0; i < length; i++) {
                        if (document.getDocuments().get(i).getId().equals(result.getContents())) {

                            break;
                        }
                    }
                    if (i == length) {
                        Log.d("QR", "No hay cubo");
                    }
                }
            }
        });

        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    int length = document.getDocuments().size();
                    final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    int i;

                    for (i = 0; i < length; i++) {
                        if (document.getDocuments().get(i).getId().equals(mail)) {
                            final Map<String, Object> data = new HashMap<>();

                            // Se añade el ID del cubo al usuario
                            data.put("cubos", FieldValue.arrayUnion(result.getContents()));
                            db.collection("usuarios").document(mail).update(data);
                            // Actualiza los cubos y termina
                            actualizaCubos(vista);
                            break;
                        }
                    }
                    if (i == length) {
                        Log.d("QR", "No hay usuario");
                    }
                }
            }
        });
    }

    // RecyclerView

    // Se activa el recyclerView
    public void actualizaCubos(View view){

        vista = view;

        db.collection("usuarios").document(usuario.getEmail()).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e){
                        if (e != null) {
                            Log.e("Firebase", "Error al leer", e);
                        } else if (snapshot == null || !snapshot.exists()) {
                            Log.e("Firebase", "Error: documento no encontrado ");
                        } else {
                            Log.d("Firestore", "datos:" + snapshot.getData());
                            ArrayList<String> data = (ArrayList<String>) snapshot.get("cubos");
                            if(data.size() > 0 && data != null) {
                                RecyclerView recyclerView = vista.findViewById(R.id.recyclerview);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(vista.getContext()));
                                recyclerView.setAdapter(new AdaptadorCubos());
                            }
                        }
                    }
                });
    }
}