package com.doctor.zaiuss.gallery;

/**
 * Created by Zaiuss on 27/07/17.
 */

public class Cuadro {
    private String dirUrl;
    private String titulo;

    public Cuadro(String dirUrl, String titulo) {
        this.dirUrl = dirUrl;
        this.titulo = dirUrl;
    }

    public String getDirUrl() {
        return dirUrl;
    }

    public void setDirUrl(String dirUrl) {
        this.dirUrl = dirUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "Cuadro{" +
                "dirUrl='" + dirUrl + '\'' +
                ", titulo='" + titulo + '\'' +
                '}';
    }
}
