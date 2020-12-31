package com.example.recycle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recycle.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.recycle.activity.ServicioLogros.mandarNotificacion;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(LoginActivity.this,
                ServicioLogros.class));
        login();
    }
    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (usuario != null && usuario.isEmailVerified()) {
            Activity activity = this;

            db.collection("usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("logros").document("Bienvenido")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("progreso", Integer.parseInt(task.getResult().get("progreso").toString()) + 1);
                    mandarNotificacion(activity, getApplicationContext(),Integer.parseInt(task.getResult().get("progreso").toString()) + 1, 1, "Bienvenido");
                    db.collection("usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("logros").document("Bienvenido")
                            .update(datos);

                    Toast.makeText(getApplicationContext(), "Has iniciado sesión: "+usuario.getDisplayName()+
                            " - "+ usuario.getEmail(),Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setLogo(R.drawable.logoletrav3)
                            .setTheme(R.style.FirebaseUITema)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if(usuario.isEmailVerified()){
                    login();
                    finish();
                }else{
                    usuario.sendEmailVerification();
                    Toast.makeText(this, "Se ha enviado un correo de autentificación ",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            } else {
                String s = "";
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) s = "Cancelado";
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK: s="Sin conexión a Internet"; break;
                    case ErrorCodes.PROVIDER_ERROR: s="Error en proveedor"; break;
                    case ErrorCodes.DEVELOPER_ERROR: s="Error desarrollador"; break;
                    default: s="Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }
}