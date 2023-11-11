package com.als.wandersnap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Resenia extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextInputLayout tiTitulo, tiContenido, tiUbicacion;
    private ImageView ivFoto;
    private MaterialButton btnTomarFoto, btnAbrirGaleria, btnRastrearUbicacion, btnSubir;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String DEFAULT_IMAGE_URI = "@drawable/multimedia_default";
    private String currentPhotoPath;
    private boolean reseniaGuardadoExitosamente = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public Resenia() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resenia, container, false);

        tiTitulo = view.findViewById(R.id.restiTituloR);
        tiContenido = view.findViewById(R.id.restiContenidoR);
        tiUbicacion = view.findViewById(R.id.restiUbicacionR);
        ivFoto = view.findViewById(R.id.resivFoto);
        btnTomarFoto = view.findViewById(R.id.resbtnTomarFoto);
        btnAbrirGaleria = view.findViewById(R.id.resbtnSeleccionarImagen);
        btnRastrearUbicacion = view.findViewById(R.id.resbtnRastrearUbicacionR);
        btnSubir = view.findViewById(R.id.resbtnSubirR);


        btnTomarFoto.setOnClickListener(v -> tomarFoto());

        btnAbrirGaleria.setOnClickListener(v -> abrirGaleria());

        btnRastrearUbicacion.setOnClickListener(v -> rastrearUbicacion());

        btnSubir.setOnClickListener(v -> {
            if (areAllFieldsFilled()) {
                uploadImageToFirebaseStorage();
            } else {
                // Muestra un mensaje de error al usuario
                //Toast.makeText(getContext(), "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
                CustomToastUtil.showWarningToast(requireContext(), "Por favor, complete todos los campos obligatorios");
            }
        });

        return view;
    }


    private boolean areAllFieldsFilled() {
        // Verifica si todos los campos obligatorios están llenos
        return !tiTitulo.getEditText().getText().toString().isEmpty()
                && !tiContenido.getEditText().getText().toString().isEmpty()
                && !tiUbicacion.getEditText().toString().isEmpty();
    }

    private void uploadImageToFirebaseStorage() {
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
                            subirResenia(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Error al subir la imagen
                        CustomToastUtil.showErrorToast(requireContext(), "Error al subir la imagen");
                    });
        } else {
            // No hay imagen seleccionada
            CustomToastUtil.showWarningToast(requireContext(), "Por favor, selecciona una imagen");
        }
    }

    public void subirResenia(String imageUrl) {

    }

    public void rastrearUbicacion() {
        
    }


    public void tomarFoto() {
        if (hasCameraPermission()) {
            // Si tienes permisos de cámara, puedes abrir la cámara.
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
            // Si no tienes permisos de cámara, solicita los permisos.
            requestCameraPermission();
        }
    }

    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
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
                CustomToastUtil.showWarningToast(getContext(), "No se otorgaron los permisos de cámara");
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario aceptó el permiso de ubicación
                // Puedes acceder a la ubicación del dispositivo
            } else {
                // El usuario rechazó el permiso de ubicación
                // No puedes acceder a la ubicación del dispositivo
                CustomToastUtil.showWarningToast(getContext(), "No se otorgaron los permisos de ubicación");
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

}