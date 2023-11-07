package com.als.wandersnap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
        getSupportActionBar().hide();
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
            CustomToastUtil.showWarningToast(getApplicationContext(),"Debes ingresar los datos");
        } else if (TextUtils.isEmpty(email)) {
            //Toast.makeText(getApplicationContext(), "Falta ingresar el usuario", Toast.LENGTH_SHORT).show();
            CustomToastUtil.showWarningToast(getApplicationContext(),"Falta ingresar el usuario");
        } else if (TextUtils.isEmpty(password)) {
            //Toast.makeText(getApplicationContext(), "Falta ingresar la contraseña", Toast.LENGTH_SHORT).show();
            CustomToastUtil.showWarningToast(getApplicationContext(),"Falta ingresar la contraseña");
        } else {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                // Iniciar sesión con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Inicio de sesión exitoso, redirigir a la actividad principal
                                CustomToastUtil.showSuccessToast(getApplicationContext(),"Ingreso autorizado");
                                goToMainActivity();
                            } else {
                                // Error al iniciar sesión, mostrar un mensaje al usuario
                                CustomToastUtil.showErrorToast(getApplicationContext(),"Datos incorrectos");
                            }
                        });

            } else {
                CustomToastUtil.showErrorToast(getApplicationContext(),"Error");
            }
        }
        //*/
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToActivity(String className) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(this, targetClass);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            // Manejar la excepción si la clase no se encuentra
            e.printStackTrace();
        }
    }

    public void openRegistrationActivity(View view) {
        // Realiza la acción que deseas al hacer clic en "Regístrate".
        // Puedes abrir otra actividad aquí, por ejemplo.
        goToActivity("com.als.wandersnap.Registro");
    }

    public void salir(View view){
        finishAffinity();
    }
}