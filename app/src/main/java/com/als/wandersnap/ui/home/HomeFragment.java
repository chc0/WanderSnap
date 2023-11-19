package com.als.wandersnap.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.als.wandersnap.CustomToastUtil;
import com.als.wandersnap.R;
import com.als.wandersnap.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();

        // Obtén una lista de reseñas desde Firestore y actualiza la interfaz de usuario después de obtener los datos
        obtenerResenasDesdeFirestore();

        return root;
    }

    private void obtenerResenasDesdeFirestore() {
        // Referencia a la colección "Resenias" en Firestore
        db.collection("Resenias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Resena> resenas = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Log.d("Firestore", "Document data: " + document.getData());

                                String autor = document.getString("autor");
                                String titulo = document.getString("titulo");
                                Long calificacionLong = document.getLong("calificacion");
                                int cali = (calificacionLong != null) ? calificacionLong.intValue() : 0;
                                String imageUr = document.getString("imageUrl");
                                String contenido = document.getString("contenido");
                                String ubica = document.getString("ubicacion");

                                Log.d("Firestore", "autor: " + autor + ", titulo: " + titulo + ", calificacion: " + cali +
                                        ", imageUrl: " + imageUr + ", contenido: " + contenido);

                                Resena resena = new Resena(autor, cali, contenido, "", imageUr, titulo, ubica);
                                resenas.add(resena);
                            } catch (Exception e) {
                                Log.e("Firestore", "Error al obtener reseñas", e);
                            }
                        }

                        // Actualiza la interfaz de usuario después de obtener los datos
                        Filtro filtro = new Filtro(resenas, getContext());
                        // Establecer el listener en la instancia de Filtro
                        filtro.setOnFiltroCompleteListener(new Filtro.OnFiltroCompleteListener() {
                            @Override
                            public void onFiltroComplete(List<Resena> resenasFiltradas) {
                                // Este método se llamará cuando el filtro esté completo
                                actualizarInterfazUsuario(resenasFiltradas);
                            }
                        });

                        // Lanzar el proceso de filtrado (asíncrono)
                        filtro.aplicarFiltro();

                    } else {
                        Log.e("Firestore", "Error al obtener reseñas", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores de manera diferida si es necesario
                    Log.e("Firestore", "Error al obtener reseñas", e);
                });
    }

    // Método para actualizar la interfaz de usuario después de obtener los datos
    private void actualizarInterfazUsuario(List<Resena> resenas) {
        // Puedes actualizar la interfaz de usuario aquí, por ejemplo, notificar al adaptador
        // que los datos han cambiado y la lista debe actualizarse


        ResenaAdapter adapter = new ResenaAdapter(requireContext(), resenas);
        listView = requireView().findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}