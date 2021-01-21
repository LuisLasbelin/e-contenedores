package com.example.recycle.visual;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.recycle.R;
import com.example.recycle.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//-----------------------------------------------------------------------------------------------
// Opciones
//-----------------------------------------------------------------------------------------------
public class Tab3 extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);
        view.findViewById(R.id.btn_admin).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.btn_contenedores).setVisibility(View.INVISIBLE);

        ((MainActivity)getActivity()).mostrarBotones( view.findViewById(R.id.btn_admin));
        return inflater.inflate(R.layout.tab3, container, false);


    }
}
