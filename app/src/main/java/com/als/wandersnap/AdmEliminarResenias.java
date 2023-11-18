package com.als.wandersnap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdmEliminarResenias extends Fragment {

    private Spinner spnResenias;
    private TextView tvAutor, tvTitulo, tvContenido, tvUbicacion, tvFecha;
    private ImageView ivFotoR;
    private MaterialButton btnEliminar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdmEliminarResenias() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adm_eliminar_resenias, container, false);

        spnResenias = view.findViewById(R.id.admelimRspnc);
        tvAutor = view.findViewById(R.id.admelimRAutor);
        tvTitulo = view.findViewById(R.id.admelimRTitulo);
        tvContenido = view.findViewById(R.id.admelimRContenido);
        tvUbicacion = view.findViewById(R.id.admelimRUbicacion);
        tvFecha = view.findViewById(R.id.admelimRFecha);
        ivFotoR = view.findViewById(R.id.admelimRFoto);
        btnEliminar = view.findViewById(R.id.admelimRbtnEliminar);

        loadSpinnerData(); // Cargar datos en el Spinner

        spnResenias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnResenias.getSelectedItemPosition() == 0) {
                    // Restablece los datos en blanco
                    resetReseniaDetails();
                    Log.e("Estado", "Opcion cero del spinner "+spnResenias.getSelectedItemPosition());

                } else {
                    if (spnResenias.getSelectedItemPosition() > 0){
                        // Si se selecciona un libro real, muestra sus datos
                        mostrarInformacionResenia(spnResenias.getSelectedItem().toString());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No es necesario implementar nada aquí
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnResenias.getSelectedItemPosition() == 0) {
                    // Si se selecciona "Seleccione un libro," muestra un mensaje de error
                    CustomToastUtil.showWarningToast(requireContext(), "Selecciona una reseña válida");
                } else {
                    // Continúa con la eliminación del libro
                    eliminarResenia(spnResenias.getSelectedItem().toString());
                }
            }
        });

        return view;
    }

    private void loadSpinnerData() {
        // Recuperar la lista de títulos de libros desde Firebase Firestore
        db.collection("Resenias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> reseniasTitle = new ArrayList<>();
                    reseniasTitle.add("Seleccione una reseña");

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String titulo = document.getString("titulo");
                        reseniasTitle.add(titulo);
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, reseniasTitle);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnResenias.setAdapter(dataAdapter);
                    Log.e("Buscar", "Spinner cargado");
                } else {
                    // Manejar errores al obtener los datos de Firebase Firestore
                }
            }
        });
    }

    private void resetReseniaDetails() {
        tvAutor.setText("No encontrado");
        tvTitulo.setText("No encontrado");
        tvContenido.setText("No encontrado");
        tvUbicacion.setText("No encontrada");
        tvFecha.setText("No encontrada");
        ivFotoR.setImageResource(R.drawable.multimedia_default);
    }

    private void mostrarInformacionResenia(String reseniaTitle) {
        Log.e("Buscar", "Entrando al método de mostrar");
        db.collection("Resenias")
                .whereEqualTo("titulo", reseniaTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                tvAutor.setText(document.getString("autor"));
                                tvTitulo.setText(document.getString("titulo"));
                                tvContenido.setText(document.getString("contenido"));
                                tvUbicacion.setText(document.getString("ubicacion"));
                                tvFecha.setText(document.getString("fecha"));

                                String imagenPath = document.getString("imageUrl");
                                //Log.e("Buscar", imagenPath);
                                Log.e("Buscar", "Información cargada");
                                if (imagenPath != null) {
                                    // Cargar la imagen utilizando Glide o la biblioteca que prefieras
                                    Glide.with(requireContext())
                                            .load(imagenPath) // Utiliza la URL de descarga de la imagen
                                            .into(ivFotoR);
                                    Log.e("Buscar", "Foto del libro");
                                } else {
                                    ivFotoR.setImageResource(R.drawable.multimedia_default);
                                    Log.e("Buscar", "Foto por defecto");
                                }
                            }
                        } else {
                            // Manejar errores al obtener los datos del libro desde Firebase Firestore
                            Log.e("Buscar", "Error al mostrar el usuario", task.getException());
                        }
                    }
                });
    }

    private void eliminarResenia(String reseniaTitle) {
        // Elimina el libro de Firebase Firestore
        db.collection("Resenias")
                .whereEqualTo("titulo", reseniaTitle)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            db.collection("Resenias").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualiza el Spinner después de la eliminación
                                        loadSpinnerData();
                                        resetReseniaDetails();
                                        CustomToastUtil.showSuccessToast(requireContext(), "Reseña eliminada correctamente");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Manejar errores al eliminar el libro
                                        CustomToastUtil.showErrorToast(requireContext(), "Error al eliminar la reseña");
                                    });
                        }
                    }
                });
    }
}