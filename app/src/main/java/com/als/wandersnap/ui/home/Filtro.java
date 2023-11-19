package com.als.wandersnap.ui.home;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Filtro {

    private final List<Resena> resenas;
    private final Context context;
    private OnFiltroCompleteListener mListener;
    private int tasksCompleted = 0;

    public interface OnFiltroCompleteListener {
        void onFiltroComplete(List<Resena> resenasFiltradas);
    }

    public Filtro(List<Resena> resenas, Context context) {
        this.resenas = resenas;
        this.context = context;
    }

    public void setOnFiltroCompleteListener(OnFiltroCompleteListener listener) {
        this.mListener = listener;
    }

    public void aplicarFiltro() {
        List<Resena> resenasFiltradas = new ArrayList<>();

        for (Resena currentResena : resenas) {
            GeocodingAndLocationTask geocodingTask = new GeocodingAndLocationTask(context, new GeocodingAndLocationTask.GeocodingAndLocationCallback() {
                @Override
                public void onGeocodingAndLocationSuccess() {
                    // La ubicación está dentro del rango
                    // Realiza acciones aquí
                    Log.d("GeocodingTask", "La ubicación está dentro del rango");
                    // Agregar la Resena a la lista filtrada
                    resenasFiltradas.add(currentResena);
                    taskCompleted(resenasFiltradas);
                }

                @Override
                public void onGeocodingAndLocationFailure(String errorMessage) {
                    // La ubicación no está dentro del rango
                    // Realiza acciones aquí
                    Log.d("GeocodingTask", "Error: " + errorMessage);
                    taskCompleted(resenasFiltradas);
                }
            });

            // Ejecutar la tarea de geocodificación y ubicación con la dirección actual
            geocodingTask.execute(currentResena.getUbicacion());
        }
    }

    private synchronized void taskCompleted(List<Resena> resenasFiltradas) {
        tasksCompleted++;

        if (tasksCompleted == resenas.size()) {
            // Todas las tareas están completas, notificar cambios en la interfaz de usuario
            notifyDataSetChanged(resenasFiltradas);
        }
    }

    private void notifyDataSetChanged(List<Resena> resenasFiltradas) {
        // Implementa la lógica para notificar al adaptador de cambios, si es necesario
        // Por ejemplo, si estás usando un adaptador de lista:
        // myListAdapter.setData(resenasFiltradas);
        // myListAdapter.notifyDataSetChanged();

        // Llamar al listener para notificar que el filtro ha sido aplicado
        if (mListener != null) {
            mListener.onFiltroComplete(resenasFiltradas);
        }
    }
}
