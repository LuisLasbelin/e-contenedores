package com.example.recycle.visual;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.recycle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

//-----------------------------------------------------------------------------------------------
// Mapa
//-----------------------------------------------------------------------------------------------
public class Tab1 extends Fragment {

    // Variables del mapa
    private GoogleMap mapa;
    private final LatLng UPV = new LatLng(39.481106, -0.340987);

    private static final int LOCATION_PERMISSION = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inicia la vista
        View view = inflater.inflate(R.layout.tab1, container, false);

        // Permission check
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            // Inicia el fragmento de mapa
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);

            // Async map
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(final GoogleMap googleMap) {

                    // Posicion actual
                    Location currentLocation = googleMap.getLas


                }
            });
        }
        else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION, getActivity());
        }

        // Return view
        return view;
    }

    // SOLICITUD DE PERMISOS

    @Override public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Function
            }
        }
    }

    public static void solicitarPermiso(final String permiso, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle(R.string.permission_request)
                    .setMessage(R.string.no_permission)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }}).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

}
