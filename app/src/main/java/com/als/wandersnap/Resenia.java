package com.als.wandersnap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

        TextInputEditText resetUbicacionR = view.findViewById(R.id.resetUbicacionR);
        resetUbicacionR.setOnClickListener(v -> buscarUbicacion(v));

        btnSubir.setOnClickListener(v -> {
            if (areAllFieldsFilled()) {
                uploadImageToFirebaseStorage();
            } else {
                CustomToastUtil.showWarningToast(requireContext(), "Por favor, complete todos los campos obligatorios");
            }
        });

        return view;
    }

    private boolean areAllFieldsFilled() {
        return !tiTitulo.getEditText().getText().toString().isEmpty()
                && !tiContenido.getEditText().getText().toString().isEmpty()
                && !tiUbicacion.getEditText().getText().toString().isEmpty();
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            String imageFileName = "images/" + UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(imageFileName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            subirResenia(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        CustomToastUtil.showErrorToast(requireContext(), "Error al subir la imagen");
                    });
        } else {
            CustomToastUtil.showWarningToast(requireContext(), "Por favor, selecciona una imagen");
        }
    }

    public void subirResenia(String imageUrl) {
        // Obten los valores de los campos del formulario
        String titulo = tiTitulo.getEditText().getText().toString();
        String contenido = tiContenido.getEditText().getText().toString();
        String ubicacion = tiUbicacion.getEditText().getText().toString();

        // Puedes agregar más campos según sea necesario

        // Crea un objeto Resenia con los datos del formulario
        ReseniaModel resenia = new ReseniaModel(titulo, contenido, ubicacion, imageUrl);

        // Conecta con Firestore y agrega la reseña
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("resenia")  // Reemplaza "tu_coleccion" con el nombre de tu colección en Firestore
                .add(resenia)
                .addOnSuccessListener(documentReference -> {
                    CustomToastUtil.showSuccessToast(requireContext(), "Reseña subida exitosamente");
                    // Puedes realizar más acciones después de subir la reseña si es necesario
                    // Por ejemplo, limpiar el formulario o navegar a otra pantalla
                })
                .addOnFailureListener(e -> {
                    CustomToastUtil.showErrorToast(requireContext(), "Error al subir la reseña: " + e.getMessage());
                });
    }


    public void rastrearUbicacion() {
        if (hasLocationPermission()) {
            LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null) {
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();

                        String mensaje = "Latitud: " + latitude + ", Longitud: " + longitude;
                        CustomToastUtil.showSuccessToast(requireContext(), mensaje);

                        obtenerDireccionDesdeCoordenadas(latitude, longitude, getView());
                    } else {
                        CustomToastUtil.showWarningToast(requireContext(), "No se pudo obtener la ubicación actual");
                    }
                } else {
                    CustomToastUtil.showWarningToast(requireContext(), "No se otorgaron los permisos de ubicación");
                }
            } else {
                CustomToastUtil.showWarningToast(requireContext(), "El proveedor de ubicación no está habilitado");
            }
        } else {
            requestLocationPermission();
        }
    }

    private void obtenerDireccionDesdeCoordenadas(double latitude, double longitude, View rootView) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                TextInputEditText ubicacionEditText = rootView.findViewById(R.id.resetUbicacionR);
                ubicacionEditText.setText(fullAddress);
            } else {
                CustomToastUtil.showWarningToast(requireContext(), "No se pudo obtener la dirección");
            }
        } catch (IOException e) {
            e.printStackTrace();
            CustomToastUtil.showErrorToast(requireContext(), "Error al obtener la dirección: " + e.getMessage());
        }
    }

    private void requestLocationPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (isGranted.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) && isGranted.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                buscarUbicacion(null);
            } else {
                CustomToastUtil.showWarningToast(requireContext(), "No se otorgaron los permisos de ubicación");
            }
        });
        requestPermissionLauncher.launch(permissions);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
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
                ivFoto.setImageURI(imageUri);
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
            ivFoto.setImageURI(selectedImage);
            imageUri = selectedImage;
        }
    }

    public void buscarUbicacion(View view) {
        // Verifica si tienes permisos de ubicación
        if (hasLocationPermission()) {
            // Obtén el servicio de ubicación
            LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            // Verifica si el proveedor de ubicación está habilitado
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Obtén la última ubicación conocida
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    // Verifica si la ubicación es válida
                    if (lastKnownLocation != null) {
                        double latitude = lastKnownLocation.getLatitude();
                        double longitude = lastKnownLocation.getLongitude();

                        // Aquí puedes usar la latitud y longitud para lo que necesites
                        // Por ejemplo, mostrarlas en un Toast
                        String mensaje = "Latitud: " + latitude + ", Longitud: " + longitude;
                        CustomToastUtil.showSuccessToast(requireContext(), mensaje);

                        // Ahora, puedes llamar a la función para obtener la dirección
                        obtenerDireccionDesdeCoordenadas(latitude, longitude, view);
                    } else {
                        // No se pudo obtener la última ubicación conocida
                        CustomToastUtil.showWarningToast(requireContext(), "No se pudo obtener la ubicación actual");
                    }
                } else {
                    // No tienes permisos de ubicación
                    CustomToastUtil.showWarningToast(requireContext(), "No se otorgaron los permisos de ubicación");
                }
            } else {
                // El proveedor de ubicación no está habilitado
                CustomToastUtil.showWarningToast(requireContext(), "El proveedor de ubicación no está habilitado");
            }
        } else {
            // Si no tienes permisos de ubicación, solicítalos utilizando el nuevo modelo
            requestLocationPermission();
        }
    }

}
