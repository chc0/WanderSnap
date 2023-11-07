package com.als.wandersnap;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToastUtil {
    public static void showWarningToast(Context context, String message) {
        showCustomToast(context, message, R.layout.custom_warning_toast_layout, R.id.cwtText);
    }

    public static void showErrorToast(Context context, String message) {
        showCustomToast(context, message, R.layout.custom_error_toast_layout, R.id.cetText);
    }

    public static void showSuccessToast(Context context, String message) {
        showCustomToast(context, message, R.layout.custom_success_toast_layout, R.id.cstText);
    }

    private static void showCustomToast(Context context, String message, int layoutResId, int textViewId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customToastView = inflater.inflate(layoutResId, null);

        // Configurar el mensaje
        TextView textView = customToastView.findViewById(textViewId);
        textView.setText(message);

        Toast customToast = new Toast(context);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER, 0, 0);
        customToast.setView(customToastView);
        customToast.show();
    }
}
