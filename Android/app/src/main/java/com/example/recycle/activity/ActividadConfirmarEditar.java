package com.example.recycle.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.recycle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActividadConfirmarEditar extends Activity implements LocationListener {
    // Firestore
    private FirebaseFirestore db = null;
    private FirebaseUser usuario = null;

    private Button guardar;
    private Button ubicacion;
    // Localizacion
    private LocationManager manejador;
    private String proveedor;
    String nombreCubo;

    String cuboID = null;
    Activity activity = null;

    private static final long TIEMPO_MIN = 10 * 1000; // 10 segundos
    private static final long DISTANCIA_MIN = 5; // 5 metros

    public Location posicion;

    //Foto
    private Button añadirFoto;
    private Button borrarFoto;
    final static int RESULTADO_BORRAR = 2;
    final static int RESULTADO_FOTO = 4;
    private Uri uriUltimaFoto;
    private ImageView fotoCubo;
    private StorageReference storageRef;
    private String direccionFoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_editar_cubo);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        cuboID = bundle.getString("cuboID");
        activity = this;
        db = FirebaseFirestore.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        fotoCubo = findViewById(R.id.fotoCubo);
        storageRef = FirebaseStorage.getInstance().getReference();

        // Cogemos los datos del cubo y rellenamos las casillas
        db.collection("cubos").document(cuboID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    EditText editTextNombre = findViewById(R.id.editTextNombre);
                    editTextNombre.setText(task.getResult().get("nombre").toString());
                    if (!task.getResult().get("foto").toString().equals("")) {
                        fotoCubo.setImageURI(Uri.parse(task.getResult().get("foto").toString()));

                        uriUltimaFoto = Uri.parse(task.getResult().get("foto").toString());
                        direccionFoto = uriUltimaFoto.getLastPathSegment();
                        Log.d("Fotos", direccionFoto);
                    }
                }
            }
        });
        //Asignamos un listener al boton guardar ubicacion
        ubicacion = findViewById(R.id.btn_ubicacion);
        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si el usuario no existe, tomamos su posicion actual y la guardamos como default
                if(posicion != null) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("longud", posicion.getLongitude());
                    datos.put("latitud", posicion.getLatitude());
                    db.collection("cubos").document(cuboID).update(datos);
                    Toast.makeText(getBaseContext(),"Se ha guardado la ubicacion con éxito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), R.string.gps_off, Toast.LENGTH_LONG).show();
                }
            }
        });
        // Asignamos un listener al boton guardar cubo
        guardar= findViewById(R.id.btn_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextNombre = findViewById(R.id.editTextNombre);
                nombreCubo = editTextNombre.getText().toString();
                Map<String, Object> nombre = new ArrayMap<>();
                nombre.put("nombre",nombreCubo);
                nombre.put("foto", uriUltimaFoto.toString());
                db.collection("cubos").document(cuboID).update(nombre);
                Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                    "Se han guardado los cambios", Toast.LENGTH_SHORT);
                    toast1.show();
                // reinicia main activity
                Intent i = new Intent(activity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                finish();
            }
        });


        // Asignamos un listener al boton añadir foto
        añadirFoto = findViewById(R.id.btn_anyadirFoto);
        añadirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    hacerFoto();
                }
                else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RESULTADO_FOTO);
                    hacerFoto();
                }
            }
        });

        borrarFoto = findViewById(R.id.btn_borrarFoto);
        borrarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFoto();
            }
        });
    }

    private void hacerFoto() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                direccionFoto = "img_" + (System.currentTimeMillis()/ 1000);
                File file = File.createTempFile(direccionFoto, ".jpg" ,
                        activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                if (Build.VERSION.SDK_INT >= 24) {
                    uriUltimaFoto = FileProvider.getUriForFile(
                            activity, "team1.1.recycle.fileProvider", file);
                    direccionFoto = uriUltimaFoto.getLastPathSegment();
                } else {
                    direccionFoto = Uri.fromFile(file).getLastPathSegment();
                    uriUltimaFoto = Uri.fromFile(file);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra (MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
                activity.startActivityForResult(intent, RESULTADO_FOTO);
            } catch (IOException ex) {
                Toast.makeText(activity, "Error al crear fichero de imagen",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void eliminarFoto() {
        StorageReference referenciaImagen = storageRef.child("imagenes/" + direccionFoto);
        referenciaImagen.delete()
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object object) {
                        uriUltimaFoto = Uri.parse("");
                        Map<String, Object> foto = new HashMap<>();
                        foto.put("foto", "");
                        db.collection("cubos").document(cuboID).update(foto);
                        fotoCubo.setImageURI(uriUltimaFoto);
                        Log.d("Fotos", "Se ha borrado");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Error al subir el fichero
                        Log.d("Fotos", "no va");
                    }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULTADO_FOTO && resultCode == RESULT_OK) {

            StorageReference ficheroRef = storageRef.child("imagenes/"+ direccionFoto);
            ficheroRef.putFile(uriUltimaFoto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Log.d("Almacenamiento", "Fichero subido");
                            fotoCubo.setImageURI(uriUltimaFoto);
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

    @Override
    public void onBackPressed() {
        eliminarFoto();
        Intent i = new Intent(activity, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        finish();
    }
    @SuppressLint("MissingPermission")
    @Override protected void onResume() {
        super.onResume();
        //Localizacion
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        posicion = manejador.getLastKnownLocation(proveedor);
        manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN,
                this);
    }
    @Override protected void onPause() {
        super.onPause();
        manejador.removeUpdates(this);
    }

    // Métodos de la interfaz LocationListener
    public void onLocationChanged(Location location) {

    }
    public void onProviderDisabled(String proveedor) {

    }
    public void onProviderEnabled(String proveedor) {

    }
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {

    }

}
