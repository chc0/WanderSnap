package com.als.wandersnap.ui.home;

public class Resena {
    private String autor;
    private float calificacion;
    private String contenido;
    private String fecha;
    private String imageUrl;
    private String titulo;
    private String ubicacion;

    // Constructor vacío requerido para Firestore
    public Resena() {
    }

    // Constructor con parámetros
    public Resena(String autor, float calificacion, String contenido, String fecha, String imageUrl, String titulo, String ubicacion) {
        this.autor = autor;
        this.calificacion = calificacion;
        this.contenido = contenido;
        this.fecha = fecha;
        this.imageUrl = imageUrl;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
    }

    // Métodos getters y setters

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
