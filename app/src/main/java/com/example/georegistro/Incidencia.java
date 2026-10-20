package com.example.georegistro;


public class Incidencia {
    private String titulo;
    private String categoria;
    private String descripcion;
    private String fechaHora;
    private String coordenadas;

    public Incidencia() {
    }
    public Incidencia(String titulo, String categoria, String descripcion, String fechaHora, String coordenadas) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.coordenadas = coordenadas;
    }
    public String getTitulo() { return titulo; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getFechaHora() { return fechaHora; }
    public String getCoordenadas() { return coordenadas; }
}