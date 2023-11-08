package com.als.wandersnap;

import java.util.regex.Pattern;

public class ValidationUtil {
    public static boolean isValidName(String name) {
        return name.matches("^[A-Za-zÁáÉéÍíÓóÚúÜüÑñ ]{5,30}$");
    }

    public static boolean isValidUsername(String username) {
        return username.matches("^[A-Za-z0-9_]{5,20}$");
    }

    public static boolean isValidEmail(String email) {
        // Este patrón verifica que el email tenga un formato válido.
        // Ten en cuenta que este patrón es una simplificación y puede no cubrir todos los casos.
        String emailPattern = "^[A-Za-z0-9+_.-]+@+[a-z.]+[.]+[a-z]+$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Puedes agregar tus propios criterios de validez para la contraseña aquí.
        // Por ejemplo, puedes requerir que tenga al menos 8 caracteres, una letra mayúscula, una letra minúscula y un número.
        return password.length() >= 6 && password.length() <= 15;
    }

    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
