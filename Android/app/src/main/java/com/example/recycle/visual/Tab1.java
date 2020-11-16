package com.example.recycle.visual;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recycle.R;
import com.example.recycle.activity.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

//-----------------------------------------------------------------------------------------------
// Mapa
//-----------------------------------------------------------------------------------------------
public class Tab1 extends Fragment {

    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inicia la vista
        View view = inflater.inflate(R.layout.tab1, container, false);

        ((MainActivity)getActivity()).mapTabLoaded((SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapa));

        // Return view
        return view;
    }

}
