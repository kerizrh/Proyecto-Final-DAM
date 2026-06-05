package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articulos")
public class Articulo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String descripcion;
    public double precio;
    public String imagePath; // Ruta local o URI de la imagen
}
