package com.als.wandersnap.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingAndLocationTask extends AsyncTask<String, Void, Boolean> {
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 123;

    private Context context;
    private GeocodingAndLocationCallback callback;

    public GeocodingAndLocationTask(Context context, GeocodingAndLocationCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // Verificar y solicitar permisos si es necesario
        if (checkLocationPermissions()) {
            String address = params[0];
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();

                    // Obtener la ubicación actual y verificar la distancia
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                    try {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();

                            float[] distance = new float[2];
                            Location.distanceBetween(latitude, longitude, currentLatitude, currentLongitude, distance);

                            // Verificar si la distancia es menor o igual a 10 kilómetros
                            return distance[0] <= 10000;  // Distancia en metros (10 kilómetros = 10000 metros)
                        }
                    } catch (SecurityException e) {
                        Log.e("GeocodingAndLocationTask", "Error obtaining location", e);
                    }
                }
            } catch (IOException e) {
                Log.e("GeocodingTask", "Error obtaining coordinates from address", e);
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            callback.onGeocodingAndLocationSuccess();
        } else {
            callback.onGeocodingAndLocationFailure("La ubicación actual no está dentro del rango de 10 kilómetros.");
        }
    }

    private boolean checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Si no se tienen los permisos, solicitarlos al usuario
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Mostrar un diálogo explicativo (esto es opcional, puedes personalizarlo según tus necesidades)
                showExplanationDialog();
            } else {
                // Solicitar permisos
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            }
            return false;
        }
    }

    private void showExplanationDialog() {
        // Aquí puedes mostrar un diálogo explicativo antes de solicitar permisos
        new AlertDialog.Builder(context)
                .setTitle("Permiso necesario")
                .setMessage("Necesitamos permisos de ubicación para continuar.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Solicitar permisos después de que el usuario acepta
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Manejar la cancelación según tus necesidades
                    // Puedes mostrar un mensaje al usuario o cerrar la actividad, por ejemplo.
                    // finish();
                })
                .show();
    }

    public interface GeocodingAndLocationCallback {
        void onGeocodingAndLocationSuccess();
        void onGeocodingAndLocationFailure(String errorMessage);
    }
}
