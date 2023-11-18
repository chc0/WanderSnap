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

public class AdmEliminarUsuarios extends Fragment {

    private Spinner spnUsuarios;
    private TextView tvNombre, tvUsuario, tvEmail, tvRol;
    private ImageView ivFotoU;
    private MaterialButton btnEliminar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdmEliminarUsuarios() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adm_eliminar_usuarios, container, false);

        spnUsuarios = view.findViewById(R.id.admelimUspnc);
        tvNombre = view.findViewById(R.id.admelimUNombre);
        tvUsuario = view.findViewById(R.id.admelimUUsuario);
        tvEmail = view.findViewById(R.id.admelimUEmail);
        tvRol = view.findViewById(R.id.admelimURol);
        ivFotoU = view.findViewById(R.id.admelimUFoto);
        btnEliminar = view.findViewById(R.id.admelimUbtnEliminar);

        loadSpinnerData(); // Cargar datos en el Spinner

        spnUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnUsuarios.getSelectedItemPosition() == 0) {
                    // Restablece los datos en blanco
                    resetUserDetails();
                    Log.e("Estado", "Opcion cero del spinner "+spnUsuarios.getSelectedItemPosition());

                } else {
                    if (spnUsuarios.getSelectedItemPosition() > 0){
                        // Si se selecciona un libro real, muestra sus datos
                        mostrarInformacionUsuario(spnUsuarios.getSelectedItem().toString());
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
                if (spnUsuarios.getSelectedItemPosition() == 0) {
                    // Si se selecciona "Seleccione un libro," muestra un mensaje de error
                    CustomToastUtil.showWarningToast(requireContext(), "Selecciona un usuario válido");
                } else {
                    // Continúa con la eliminación del libro
                    eliminarUsuario(spnUsuarios.getSelectedItem().toString());
                }
            }
        });

        return view;
    }

    private void loadSpinnerData() {
        // Recuperar la lista de títulos de libros desde Firebase Firestore
        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> usersName = new ArrayList<>();
                    usersName.add("Seleccione un usuario");

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String usuario = document.getString("usuario");
                        usersName.add(usuario);
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, usersName);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnUsuarios.setAdapter(dataAdapter);
                    Log.e("Buscar", "Spinner cargado");
                } else {
                    // Manejar errores al obtener los datos de Firebase Firestore
                }
            }
        });
    }

    private void resetUserDetails() {
        tvNombre.setText("No encontrado");
        tvUsuario.setText("No encontrado");
        tvEmail.setText("No encontrado");
        tvRol.setText("No encontrado");
        ivFotoU.setImageResource(R.drawable.usuario_default);
    }

    private void mostrarInformacionUsuario(String usuarioNombre) {
        Log.e("Buscar", "Entrando al método de mostrar");
        db.collection("Usuarios")
                .whereEqualTo("usuario", usuarioNombre)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                tvNombre.setText(document.getString("nombre"));
                                tvUsuario.setText(document.getString("usuario"));
                                tvEmail.setText(document.getString("email"));
                                tvRol.setText(document.getString("rol"));

                                String imagenPath = document.getString("foto_perfil");
                                //Log.e("Buscar", imagenPath);
                                Log.e("Buscar", "Información cargada");
                                if (imagenPath != null) {
                                    // Cargar la imagen utilizando Glide o la biblioteca que prefieras
                                    Glide.with(requireContext())
                                            .load(imagenPath) // Utiliza la URL de descarga de la imagen
                                            .into(ivFotoU);
                                    Log.e("Buscar", "Foto del libro");
                                } else {
                                    ivFotoU.setImageResource(R.drawable.usuario_default);
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

    private void eliminarUsuario(String userName) {
        // Elimina el libro de Firebase Firestore
        db.collection("Usuarios")
                .whereEqualTo("usuario", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            db.collection("Usuarios").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualiza el Spinner después de la eliminación
                                        loadSpinnerData();
                                        resetUserDetails();
                                        CustomToastUtil.showSuccessToast(requireContext(), "Usuario eliminado correctamente");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Manejar errores al eliminar el libro
                                        CustomToastUtil.showErrorToast(requireContext(), "Error al eliminar el usuario");
                                    });
                        }
                    }
                });
    }
}