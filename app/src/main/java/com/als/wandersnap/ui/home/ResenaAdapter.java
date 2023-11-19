package com.als.wandersnap.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.als.wandersnap.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ResenaAdapter extends ArrayAdapter<Resena> {

    private final LayoutInflater inflater;

    public ResenaAdapter(Context context, List<Resena> resenas) {
        super(context, 0, resenas);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.inputresenia, parent, false);
        }

        // Obtener la reseña en la posición actual
        Resena currentResena = getItem(position);

        // Obtener referencias a las vistas en el diseño del elemento de la lista
        ImageView imageView = itemView.findViewById(R.id.imageView3);
        TextView tvTitulo = itemView.findViewById(R.id.tvTitulo);
        TextView tvAutor = itemView.findViewById(R.id.tvAutor);
        TextView tvCalificacion = itemView.findViewById(R.id.tvCalificacion);
        TextView tvUbi = itemView.findViewById(R.id.tvUbi);

        // Configurar las vistas con los datos de la reseña actual
        // Puedes obtener otros campos de la reseña de manera similar
        tvTitulo.setText(currentResena.getTitulo());
        tvAutor.setText("Autor: " + currentResena.getAutor());
        Log.d("Datos de autor", "autor es: "+currentResena.getAutor());
        tvCalificacion.setText("Contenido: " + currentResena.getContenido());
        tvUbi.setText("Ubicación:"+currentResena.getUbicacion());

        // Cargar la imagen usando Glide
        Glide.with(getContext())
                .load(currentResena.getImageUrl())
                .into(imageView);

        return itemView;
    }
}
