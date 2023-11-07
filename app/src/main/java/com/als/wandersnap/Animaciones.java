package com.als.wandersnap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class Animaciones {
    public void animarBotonRegistro(View vista) {
        // Anima el botón de registro
        ObjectAnimator animacion = ObjectAnimator.ofFloat(vista, "rotation", 0f, 90f);
        animacion.setDuration(500);
        animacion.start();
    }

    public void animarTransicion(View vista1, View vista2) {
        // Anima la transición entre dos secciones del formulario
        AnimatorSet animacion = new AnimatorSet();
        animacion.playTogether(
                ObjectAnimator.ofFloat(vista1, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(vista2, "alpha", 0f, 1f)
        );
        animacion.setDuration(500);
        animacion.start();
    }
}
