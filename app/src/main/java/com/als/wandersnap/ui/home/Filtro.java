package com.als.wandersnap.ui.home;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Clase Filtro
public class Filtro {

    private final List<Resena> resenas;
    private final Context context;

    public Filtro(List<Resena> resenas, Context context) {
        this.resenas = resenas;
        this.context = context;
    }

    public List<Resena> aplicarFiltro() {
        List<Resena> resenasFiltradas = new ArrayList<>();

        for (Resena currentResena : resenas) {
            // Realizar la tarea de geocodificación y ubicación
            GeocodingAndLocationTask geocodingTask = new GeocodingAndLocationTask(context, new GeocodingAndLocationTask.GeocodingAndLocationCallback() {
                @Override
                public void onGeocodingAndLocationSuccess() {
                    // La ubicación está dentro del rango
                    // Realiza acciones aquí
                    Log.d("GeocodingTask", "La ubicación está dentro del rango");
                    // Agregar la Resena a la lista filtrada
                    resenasFiltradas.add(currentResena);
                    notifyDataSetChanged();  // Actualizar la interfaz de usuario
                }

                @Override
                public void onGeocodingAndLocationFailure(String errorMessage) {
                    // La ubicación no está dentro del rango
                    // Realiza acciones aquí
                    Log.d("GeocodingTask", "Error: " + errorMessage);
                    //bandera = false;
                    notifyDataSetChanged();  // Actualizar la interfaz de usuario
                }
            });

            // Ejecutar la tarea de geocodificación y ubicación con la dirección actual
            boolean result;
            try {
                result = geocodingTask.execute(currentResena.getUbicacion()).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                result = false; // Manejar el error, por ejemplo, establecer result en false
            }
        }

        return resenasFiltradas;
    }

    private void notifyDataSetChanged() {
        // Implementa la lógica para notificar al adaptador de cambios, si es necesario
        // Puedes usar una interfaz o algún otro mecanismo para comunicarte con la clase que llama a este método
    }
}
