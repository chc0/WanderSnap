package com.als.wandersnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class Registro extends AppCompatActivity {

    private TextInputLayout tiNombre, tiUsuario, tiEmail, tiContrasenia, tiConfirmarContrasenia;
    private ImageView ivFoto;
    private MaterialButton btnTomarFoto;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String DEFAULT_IMAGE_URI = "@drawable/usuario_default";
    private String currentPhotoPath;
    private boolean usuarioGuardadoExitosamente = false;
    private boolean emailError, usernameError = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registro);

        tiNombre = findViewById(R.id.regtiNombre);
        tiUsuario = findViewById(R.id.regtiUsuario);
        tiEmail = findViewById(R.id.regtiEmail);
        tiContrasenia = findViewById(R.id.regtiContrasenia);
        tiConfirmarContrasenia = findViewById(R.id.regtiConfirmarContrasenia);
        ivFoto = findViewById(R.id.regivFoto);
        btnTomarFoto = findViewById(R.id.regbtnTomarFoto);

        btnTomarFoto.setOnClickListener(v -> tomarFoto());

        RealTimeValidationUtil.setupNameValidation(tiNombre);
        RealTimeValidationUtil.setupUsernameValidation(tiUsuario);
        RealTimeValidationUtil.setupEmailValidation(tiEmail);
        RealTimeValidationUtil.setupPasswordValidation(tiContrasenia);
        RealTimeValidationUtil.setupConfirmPasswordValidation(tiConfirmarContrasenia, tiContrasenia);
    }

    public void registrar(View view) {
        if (areAllFieldsFilled()) {
            String email = tiEmail.getEditText().getText().toString();
            String usuario = tiUsuario.getEditText().getText().toString();

            // Realizar consultas para verificar la disponibilidad del correo y el usuario
            checkIfEmailAndUsernameExist(email, usuario);
        } else {
            // Muestra un mensaje de error al usuario
            CustomToastUtil.showWarningToast(getApplicationContext(), "Por favor, complete todos los campos");
        }
    }


    private boolean areAllFieldsFilled() {
        // Verifica si todos los campos obligatorios están llenos
        return !tiNombre.getEditText().getText().toString().isEmpty()
                && !tiUsuario.getEditText().getText().toString().isEmpty()
                && !tiEmail.getEditText().getText().toString().isEmpty()
                && !tiContrasenia.getEditText().getText().toString().isEmpty()
                && !tiConfirmarContrasenia.getEditText().getText().toString().isEmpty();
    }

    private void checkIfEmailAndUsernameExist(String email, String username) {
        // Variables para llevar un registro de los campos con errores

        // Consulta para verificar si el correo electrónico ya existe
        db.collection("Usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(emailQueryDocumentSnapshots -> {
                    if (!emailQueryDocumentSnapshots.isEmpty()) {
                        // El correo electrónico ya está en uso
                        tiEmail.setError("Correo electrónico ya registrado");
                        emailError = true;
                    } else {
                        emailError = false;
                    }

                    // Consulta para verificar si el nombre de usuario ya existe
                    db.collection("Usuarios")
                            .whereEqualTo("usuario", username)
                            .get()
                            .addOnSuccessListener(usernameQueryDocumentSnapshots -> {
                                if (!usernameQueryDocumentSnapshots.isEmpty()) {
                                    // El nombre de usuario ya está en uso
                                    tiUsuario.setError("Nombre de usuario ya registrado");
                                    usernameError = true;
                                }
                                else {
                                    usernameError = false;
                                }

                                // Verifica si hubo errores en la validación
                                if (emailError || usernameError) {
                                    // Hubo errores, no continuar con el registro
                                    CustomToastUtil.showWarningToast(getApplicationContext(), "Corrija los errores");
                                } else {
                                    // No hubo errores, puedes continuar con el registro
                                    tiEmail.setErrorEnabled(false);
                                    tiUsuario.setErrorEnabled(false);
                                    // Continuar con el proceso de registro
                                    // ...
                                    uploadImageToFirebaseStorage(emailError, usernameError);
                                    // registrarUsuario();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Manejar errores de consulta de nombre de usuario, si es necesario
                                Log.e("Error", "Error en la consulta de nombre de usuario: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Manejar errores de consulta de correo electrónico, si es necesario
                    Log.e("Error", "Error en la consulta de correo electrónico: " + e.getMessage());
                });
    }

    private void uploadImageToFirebaseStorage(Boolean emailError, Boolean usernameError) {
        if (imageUri != null) {
            // Crea una referencia en Firebase Storage con un nombre único
            String imageFileName = "images/" + UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(imageFileName);

            // Sube la imagen a Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Imagen subida con éxito, obtén la URL de descarga
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Ahora puedes guardar imageUrl en Firestore como se explicó anteriormente
                            registrarUsuario(imageUrl, emailError, usernameError);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Error al subir la imagen
                        CustomToastUtil.showErrorToast(getApplicationContext(), "Error al subir la imagen");
                    });
        } else {
            // No hay imagen seleccionada
            CustomToastUtil.showWarningToast(getApplicationContext(), "Por favor, selecciona una imagen");
        }
    }

    private void registrarUsuario(String imageUrl, Boolean emailError, Boolean usernameError) {
        String nombre = tiNombre.getEditText().getText().toString();
        String usuario = tiUsuario.getEditText().getText().toString();
        String email = tiEmail.getEditText().getText().toString();
        String contrasenia = tiContrasenia.getEditText().getText().toString();
        String confirmarContrasenia = tiConfirmarContrasenia.getEditText().getText().toString();

        String imagenPath = (imageUrl != null) ? imageUrl.toString() : DEFAULT_IMAGE_URI;

        // Validaciones
        if (emailError || usernameError || !ValidationUtil.isValidName(nombre) || !ValidationUtil.isValidUsername(usuario) ||
                !ValidationUtil.isValidEmail(email) || !ValidationUtil.isValidPassword(contrasenia) ||
                !ValidationUtil.passwordsMatch(contrasenia, confirmarContrasenia)) {
            // Mostrar un mensaje de error al usuario
            CustomToastUtil.showWarningToast(getApplicationContext(), "Los datos ingresados no son válidos");
        } else {
            // Registrar el usuario en Firestore
            Map<String, Object> usuarioFirestore = new HashMap<>();
            usuarioFirestore.put("nombre", nombre);
            usuarioFirestore.put("usuario", usuario);
            usuarioFirestore.put("email", email);
            usuarioFirestore.put("contrasenia", contrasenia);
            usuarioFirestore.put("foto_perfil", imagenPath);
            usuarioFirestore.put("rol", "normal");

            db.collection("Usuarios").add(usuarioFirestore)
                    .addOnSuccessListener(documentReference -> {
                        // Registrar el usuario en Authentication
                        mAuth.createUserWithEmailAndPassword(email, contrasenia)
                                .addOnSuccessListener(authResult -> {
                                    // Usuario registrado exitosamente
                                    CustomToastUtil.showSuccessToast(getApplicationContext(), "Usuario registrado exitosamente");
                                    // Ir a la pantalla principal
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    // Error al registrar el usuario en Authentication
                                    Log.e("WanderSnap", "Error al registrar el usuario: ", e);
                                    CustomToastUtil.showErrorToast(getApplicationContext(), "Error");

                                });
                    })
                    .addOnFailureListener(e -> {
                        // Error al registrar el usuario en Firestore
                        Log.e("WanderSnap", "Error al registrar el usuario: ", e);
                        CustomToastUtil.showErrorToast(getApplicationContext(), "Error");
                    });
        }
    }

    public void tomarFoto() {
        if (hasCameraPermission()) {
            // Si tienes permisos de cámara, puedes abrir la cámara.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(this, "com.als.wandersnap.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            // Si no tienes permisos de cámara, solicita los permisos.
            requestCameraPermission();
        }
    }

    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario otorgó el permiso, puedes tomar la foto.
                tomarFoto();
            } else {
                // El usuario rechazó el permiso, puedes mostrar un mensaje o tomar una acción adecuada.
                CustomToastUtil.showWarningToast(getApplicationContext(), "No se otorgaron los permisos de cámara");
            }
        }
    }

    public void abrirGaleria(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();  // Guarda la ruta de la imagen
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
                // Mostrar la imagen en la vista previa
                ivFoto.setImageURI(imageUri);
            } else {
                // La foto no se capturó correctamente, así que elimina el archivo temporal
                if (imageUri != null) {
                    File photoFile = new File(imageUri.getPath());
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // Si se selecciona una imagen de la galería
            Uri selectedImage = data.getData();
            ivFoto.setImageURI(selectedImage);
            imageUri = selectedImage;
        }
    }

    public void cancelar(View view){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
}