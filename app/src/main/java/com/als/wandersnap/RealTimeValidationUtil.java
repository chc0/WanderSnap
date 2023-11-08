package com.als.wandersnap;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class RealTimeValidationUtil {

    public static void setupNameValidation(final TextInputLayout tiName) {
        tiName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !ValidationUtil.isValidName(s.toString())) {
                    tiName.setError("Nombre inválido. Debe contener solo letras y espacios");
                } else {
                    tiName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setupUsernameValidation(final TextInputLayout tiUsername) {
        tiUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !ValidationUtil.isValidUsername(s.toString())) {
                    tiUsername.setError("Nombre de usuario inválido. Debe contener solo letras, números y guiones bajos");
                } else {
                    tiUsername.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setupEmailValidation(final TextInputLayout tiEmail) {
        tiEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !ValidationUtil.isValidEmail(s.toString())) {
                    tiEmail.setError("Debe tener un formato de correo electronico válido");
                } else {
                    tiEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setupPasswordValidation(final TextInputLayout tiPassword) {
        tiPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !ValidationUtil.isValidPassword(s.toString())) {
                    tiPassword.setError("Contraseña inválida. Debe tener entre 6 y 15 caracteres.");
                } else {
                    tiPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setupConfirmPasswordValidation(final TextInputLayout tiConfirmPassword, final TextInputLayout tiPassword) {
        tiConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirmPassword = s.toString();
                String password = tiPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(confirmPassword)  && !confirmPassword.equals(password)) {
                    tiConfirmPassword.setError("Las contraseñas no coinciden.");
                } else {
                    tiConfirmPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
