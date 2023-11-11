package com.als.wandersnap.ui.usuario;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.als.wandersnap.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class datosUsuario extends Fragment {

    private ImageView ivFotoPerfil;
    private TextView tvUsuario, tvEmail, tvNombre;
    private Button btnEditar;

    private String currentImageURL = "";

    private DatosUsuarioViewModel mViewModel;

    public static datosUsuario newInstance() {
        return new datosUsuario();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_datos_usuario, container, false);

        ivFotoPerfil = view.findViewById(R.id.perFotoPerfil);
        tvUsuario = view.findViewById(R.id.perUsuario);
        tvEmail = view.findViewById(R.id.perEmail);
        tvNombre = view.findViewById(R.id.perNombre);
        btnEditar = view.findViewById(R.id.perbtnEditar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // El usuario está autenticado, establece la información en los elementos de la interfaz de usuario
            String username = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query userQuery = db.collection("Usuarios").whereEqualTo("email", email);

            userQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Supongo que el correo electrónico es único en tu colección
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Obtén el nombre del usuario
                        String fullName = document.getString("nombre");
                        String user = document.getString("usuario");
                        String imagenPath = document.getString("foto_perfil");

                        // Ahora puedes usar fullName como desees
                        // Por ejemplo, establecer el texto en un TextView
                        tvNombre.setText(fullName);
                        tvUsuario.setText(user);
                        if (imagenPath != null) {
                            // Cargar la imagen utilizando Glide o la biblioteca que prefieras
                            Glide.with(requireContext())
                                    .load(imagenPath) // Utiliza la URL de descarga de la imagen
                                    .into(ivFotoPerfil);
                            currentImageURL = imagenPath;
                        } else {
                            ivFotoPerfil.setImageResource(R.drawable.usuario_default);
                        }
                        tvEmail.setText(email);
                    }
                } else {
                    // Manejar el error al obtener datos de Firestore
                }
            });



            tvEmail.setText(email);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DatosUsuarioViewModel.class);
        // TODO: Use the ViewModel
    }

}