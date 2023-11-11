package com.als.wandersnap;

public class ReseniaModel {

    private String titulo;
    private String contenido;
    private String ubicacion;
    private String imageUrl;

    // Constructor vacío requerido para Firestore
    public ReseniaModel() {
    }

    // Constructor con parámetros
    public ReseniaModel(String titulo, String contenido, String ubicacion, String imageUrl) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.ubicacion = ubicacion;
        this.imageUrl = imageUrl;
    }

    // Métodos getter y setter

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
