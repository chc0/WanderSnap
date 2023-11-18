package com.als.wandersnap.ui.usuario;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.als.wandersnap.CustomToastUtil;
import com.als.wandersnap.R;
import com.als.wandersnap.RealTimeValidationUtil;
import com.als.wandersnap.ReseniaModel;
import com.als.wandersnap.ValidationUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class datosUsuario extends Fragment {

    private ImageView ivFotoPerfil, ivNuevaFoto;
    private TextView tvUsuario, tvEmail, tvNombre;
    private TextInputLayout tiNombre, tiEmail, tiContrasenia, tiConfirmarContrasenia;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String DEFAULT_IMAGE_URI = "@drawable/usuario_default";
    private String currentPhotoPath;
    private MaterialButton btnEditar, btnTomarFoto, btnAbrirGaleria;
    private Button btnGuardarCambios;
    private LinearLayout usuarioInfoLayout, editarLayout;
    private String currentImageURL = "";
    private StorageReference stg = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
    private String email ="";

    private DatosUsuarioViewModel mViewModel;

    public datosUsuario() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_datos_usuario, container, false);

        ivFotoPerfil = view.findViewById(R.id.perFotoPerfil);
        tvUsuario = view.findViewById(R.id.perUsuario);
        tvEmail = view.findViewById(R.id.perEmail);
        tvNombre = view.findViewById(R.id.perNombre);
        tiNombre = view.findViewById(R.id.pertiNombre);
        //tiEmail = view.findViewById(R.id.pertiEmail);
        tiContrasenia = view.findViewById(R.id.pertiContrasenia);
        tiConfirmarContrasenia = view.findViewById(R.id.pertiConfirmarContrasenia);
        ivNuevaFoto = view.findViewById(R.id.perNuevaFoto);
        btnTomarFoto = view.findViewById(R.id.perbtnTomarFoto);
        btnAbrirGaleria = view.findViewById(R.id.perbtnSeleccionarImagen);
        btnEditar = view.findViewById(R.id.perbtnEditar);
        btnGuardarCambios = view.findViewById(R.id.perbtnGuardar);
        usuarioInfoLayout = view.findViewById(R.id.perdaLayout);
        editarLayout = view.findViewById(R.id.peredLayout);


        mostrarDatos();

        RealTimeValidationUtil.setupNameValidation(tiNombre);
        //RealTimeValidationUtil.setupEmailValidation(tiEmail);
        RealTimeValidationUtil.setupPasswordValidation(tiContrasenia);
        RealTimeValidationUtil.setupConfirmPasswordValidation(tiConfirmarContrasenia, tiContrasenia);

        btnEditar.setOnClickListener(v -> {
            // Oculta el layout de información de usuario
            ivFotoPerfil.setVisibility(View.GONE);
            usuarioInfoLayout.setVisibility(View.GONE);
            // Muestra el layout de edición
            editarLayout.setVisibility(View.VISIBLE);
        });
        btnGuardarCambios.setOnClickListener(v -> {
            // Realiza la lógica para guardar los cambios
            // ...
            modificar();

            // Muestra el layout de información de usuario
            //ivFotoPerfil.setVisibility(View.VISIBLE);
            //usuarioInfoLayout.setVisibility(View.VISIBLE);
            // Oculta el layout de edición
            //editarLayout.setVisibility(View.GONE);
        });

        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnAbrirGaleria.setOnClickListener(v -> abrirGaleria());



        return view;
    }

    private void mostrarDatos(){
        if (usuarioActual != null) {
            // El usuario está autenticado, establece la información en los elementos de la interfaz de usuario
            email = usuarioActual.getEmail();

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
                        //String correo = document.getString("email");
                        String contrasenia = document.getString("contrasenia");
                        String imagenPath = document.getString("foto_perfil");

                        // Ahora puedes usar fullName como desees
                        // Por ejemplo, establecer el texto en un TextView
                        tvNombre.setText(fullName);
                        tiNombre.getEditText().setText(fullName);
                        //tiEmail.getEditText().setText(correo);
                        tiContrasenia.getEditText().setText(contrasenia);
                        tiConfirmarContrasenia.getEditText().setText(contrasenia);
                        tvUsuario.setText(user);
                        if (imagenPath != null) {
                            // Cargar la imagen utilizando Glide o la biblioteca que prefieras
                            Glide.with(requireContext())
                                    .load(imagenPath) // Utiliza la URL de descarga de la imagen
                                    .into(ivFotoPerfil);
                            Glide.with(requireContext())
                                    .load(imagenPath) // Utiliza la URL de descarga de la imagen
                                    .into(ivNuevaFoto);
                            currentImageURL = imagenPath;
                        } else {
                            ivFotoPerfil.setImageResource(R.drawable.usuario_default);
                            ivNuevaFoto.setImageResource(R.drawable.usuario_default);
                        }
                        tvEmail.setText(email);
                    }
                } else {
                    // Manejar el error al obtener datos de Firestore
                    CustomToastUtil.showErrorToast(requireContext(), "Error al cargar los datos");
                }
            });
            tvEmail.setText(email);
        }
    }



    public void modificar() {
        if (areAllFieldsFilled()) {
            uploadImageToFirebaseStorage();
        } else {
            // Muestra un mensaje de error al usuario
            CustomToastUtil.showWarningToast(requireContext(), "Por favor, complete todos los campos");
        }
    }

    private boolean areAllFieldsFilled() {
        // Verifica si todos los campos obligatorios están llenos
        return !tiNombre.getEditText().getText().toString().isEmpty()
                //&& !tiEmail.getEditText().getText().toString().isEmpty()
                && !tiContrasenia.getEditText().getText().toString().isEmpty()
                && !tiConfirmarContrasenia.getEditText().getText().toString().isEmpty();
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            // Crea una referencia en Firebase Storage con un nombre único
            String imageFileName = "images/" + UUID.randomUUID().toString();
            StorageReference imageRef = stg.child(imageFileName);

            // Sube la imagen a Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Imagen subida con éxito, obtén la URL de descarga
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Ahora puedes guardar imageUrl en Firestore como se explicó anteriormente
                            modificarUsuario(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Error al subir la imagen
                        CustomToastUtil.showErrorToast(requireContext(), "Error al subir la imagen");
                    });
        } else {
            // No se ha seleccionado una nueva imagen, usa la URL de la imagen actual
            String imageUrl = currentImageURL;

            // El resto de tu lógica para actualizar el libro
            modificarUsuario(imageUrl);
        }
    }

    private void modificarUsuario(String imageUrl) {
        String nuevoNombre = tiNombre.getEditText().getText().toString();
        //String nuevoEmail = tiEmail.getEditText().getText().toString();
        String nuevaContrasenia = tiContrasenia.getEditText().getText().toString();
        String confirmarNuevaContrasenia = tiConfirmarContrasenia.getEditText().getText().toString();

        String nuevaImagenPath = (imageUrl != null) ? imageUrl : DEFAULT_IMAGE_URI;

        if (!ValidationUtil.isValidName(nuevoNombre) || !ValidationUtil.isValidPassword(nuevaContrasenia)
                //|| !ValidationUtil.isValidEmail(nuevoEmail)
                || !ValidationUtil.passwordsMatch(nuevaContrasenia, confirmarNuevaContrasenia)) {
            // Mostrar un mensaje de error al usuario
            CustomToastUtil.showWarningToast(requireContext(), "Los datos ingresados no son válidos");
            return;
        }

        // Crear un mapa con los datos actualizados
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nuevoNombre);
        //userData.put("email", nuevoEmail);
        userData.put("contrasenia", nuevaContrasenia);
        userData.put("foto_perfil", nuevaImagenPath);

        // Obtener la referencia del documento del usuario actual
        Query userQuery = db.collection("Usuarios").whereEqualTo("email", email);

        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Supongo que el correo electrónico es único en tu colección
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String userId = document.getId();

                    // Actualizar los datos del usuario en Firestore
                    db.collection("Usuarios").document(userId)
                            .update(userData)
                            .addOnSuccessListener(aVoid -> {
                                // Éxito al actualizar los datos
                                // Cambiar la contraseña en Firebase Authentication
                                cambiarContraseniaFirebaseAuthentication(nuevaContrasenia);
                            })
                            .addOnFailureListener(e -> {
                                // Error al actualizar los datos
                                CustomToastUtil.showErrorToast(requireContext(), "Error al actualizar los datos: " + e.getMessage());
                            });
                }
            } else {
                // Manejar el error al obtener datos de Firestore
                CustomToastUtil.showErrorToast(requireContext(), "Error al obtener datos del usuario: " + task.getException().getMessage());
            }
        });
    }

    private void cambiarContraseniaFirebaseAuthentication(String nuevaContrasenia) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updatePassword(nuevaContrasenia)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Éxito al cambiar la contraseña en Firebase Authentication
                            CustomToastUtil.showSuccessToast(requireContext(), "Datos actualizados correctamente");
                            mostrarDatos();
                            // Volver a mostrar el layout de información de usuario
                            ivFotoPerfil.setVisibility(View.VISIBLE);
                            usuarioInfoLayout.setVisibility(View.VISIBLE);
                            // Ocultar el layout de edición
                            editarLayout.setVisibility(View.GONE);
                        } else {
                            // Error al cambiar la contraseña en Firebase Authentication
                            CustomToastUtil.showErrorToast(requireContext(), "Error al actualizar los datos: " + task.getException().getMessage());
                            mostrarDatos();
                        }
                    });
        }
    }



    public void tomarFoto() {
        if (hasCameraPermission()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(getContext(), "com.als.wandersnap.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            requestCameraPermission();
        }
    }

    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                CustomToastUtil.showWarningToast(getContext(), "No se otorgaron los permisos de cámara");
            }
        }
    }

    public void abrirGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                ivNuevaFoto.setImageURI(imageUri);
            } else {
                if (imageUri != null) {
                    File photoFile = new File(imageUri.getPath());
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ivNuevaFoto.setImageURI(selectedImage);
            imageUri = selectedImage;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DatosUsuarioViewModel.class);
        // TODO: Use the ViewModel
    }

}