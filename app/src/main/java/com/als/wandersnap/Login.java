package com.als.wandersnap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputLayout editTextEmail, editTextPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.logetMail);
        editTextPassword = findViewById(R.id.logetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario ya ha iniciado sesión
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario ya ha iniciado sesión, redirige a la actividad principal
            goToMainActivity();
        }
    }

    public void login(View view) {
        String email = editTextEmail.getEditText().getText().toString();
        String password = editTextPassword.getEditText().getText().toString();
        //goToMainActivity();

        ///*
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            //Toast.makeText(getApplicationContext(), "Debes ingresar los datos", Toast.LENGTH_SHORT).show();
            showWCustomToast("Debes ingresar los datos");
        } else if (TextUtils.isEmpty(email)) {
            //Toast.makeText(getApplicationContext(), "Falta ingresar el usuario", Toast.LENGTH_SHORT).show();
            showWCustomToast("Falta ingresar el usuario");
        } else if (TextUtils.isEmpty(password)) {
            //Toast.makeText(getApplicationContext(), "Falta ingresar la contraseña", Toast.LENGTH_SHORT).show();
            showWCustomToast("Falta ingresar la contraseña");
        } else {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                // Iniciar sesión con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Inicio de sesión exitoso, redirigir a la actividad principal
                                showSCustomToast("Ingreso autorizado");
                                goToMainActivity();
                            } else {
                                // Error al iniciar sesión, mostrar un mensaje al usuario
                                //Toast.makeText(this, "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                                showECustomToast("Datos incorrectos");
                            }
                        });

            } else {
                //Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
                showECustomToast("Error");
            }
        }
        //*/
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void salir(View view) {
        //Toast.makeText(this, "Hasta luego.", Toast.LENGTH_SHORT).show();
        finishAffinity();
    }

    private void showWCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_warning_toast_layout, null);

        // Configurar el mensaje
        TextView textView = customToastView.findViewById(R.id.cwtText);
        textView.setText(message);

        Toast customWToast = new Toast(getApplicationContext());
        customWToast.setDuration(Toast.LENGTH_SHORT);
        customWToast.setGravity(Gravity.CENTER, 0, 0);
        customWToast.setView(customToastView);
        customWToast.show();
    }

    private void showECustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_error_toast_layout, null);

        // Configurar el mensaje
        TextView textView = customToastView.findViewById(R.id.cetText);
        textView.setText(message);

        Toast customWToast = new Toast(getApplicationContext());
        customWToast.setDuration(Toast.LENGTH_SHORT);
        customWToast.setGravity(Gravity.CENTER, 0, 0);
        customWToast.setView(customToastView);
        customWToast.show();
    }

    private void showSCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_success_toast_layout, null);

        // Configurar el mensaje
        TextView textView = customToastView.findViewById(R.id.cstText);
        textView.setText(message);

        Toast customWToast = new Toast(getApplicationContext());
        customWToast.setDuration(Toast.LENGTH_SHORT);
        customWToast.setGravity(Gravity.CENTER, 0, 0);
        customWToast.setView(customToastView);
        customWToast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            // Lógica para salir de la aplicación
            FirebaseAuth.getInstance().signOut();
            showSCustomToast("Saliendo");
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}