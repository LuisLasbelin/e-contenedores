package com.example.recycle.visual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.recycle.R;
import com.example.recycle.activity.ActividadContenedoresCalle;
import com.example.recycle.activity.ActividadUsuarios;
import com.example.recycle.activity.LoginActivity;
import com.example.recycle.activity.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflador,
                                       ViewGroup contenedor, Bundle savedInstanceState) {

        View vista = inflador.inflate(R.layout.fragment_usuario,
                contenedor, false);

       FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //==================================================================================================
        //Boton cerrar secion


        Button cerrarSesion = (Button) vista.findViewById(R.id.btn_cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
            }
        });
//==================================================================================================
//==================================================================================================

        //Foto de perfil
        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(getActivity()
                .getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });

//==================================================================================================

        // Foto de usuario
        //==========================================================================================
        Uri urlImagen = usuario.getPhotoUrl();

        if (urlImagen != null) {
            NetworkImageView fotoUsuario = (NetworkImageView)
                    vista.findViewById(R.id.imagen);
            //fotoUsuario.setImageResource(R.drawable.logoletra);
            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        //==========================================================================================


        TextView nombre = (TextView) vista.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        TextView correo = (TextView) vista.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());

        TextView nombre2 = (TextView) vista.findViewById(R.id.PhoneNumber);
        nombre2.setText(usuario.getPhoneNumber());

        TextView nombre3 = (TextView) vista.findViewById(R.id.Uid);
        nombre3.setText(usuario.getUid());


       db.collection("usuarios").document(usuario.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if(document.getData().containsValue("Admin")){

                        vista.findViewById(R.id.btn_admin2).setVisibility(View.VISIBLE);
                        vista.findViewById(R.id.btn_contenedores2).setVisibility(View.VISIBLE);

                        Button Usuarios = (Button) vista.findViewById(R.id.btn_admin2);
                        Usuarios.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {

                                                Intent i = new Intent(getActivity(), ActividadUsuarios.class);
                                                startActivity(i);
                            }
                        });

                        Button Contenedores = (Button) vista.findViewById(R.id.btn_contenedores2);
                        Contenedores.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                                Intent i = new Intent(getActivity(), ActividadContenedoresCalle.class);
                                                startActivity(i);
                            }
                        });

                    }

                } else {
                    Log.w("aaa", "Error getting documents.", task.getException());
                }
            }
        });
        return vista;

//==================================================================================================
    }
}