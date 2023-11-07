package com.als.wandersnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Registro extends AppCompatActivity {

    private TextInputLayout tiNombre;
    private TextInputEditText etNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.regetNombre);
        /*
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hace nada
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Anima la entrada del campo de texto
                Animaciones animacion = new Animaciones();
                animacion.animarEntrada(etNombre);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No hace nada
            }
        });
        */
    }

    public void cancelar(View view){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
}