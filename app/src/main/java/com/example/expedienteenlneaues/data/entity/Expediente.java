package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expedientes")
public class Expediente {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String carnet;
    public String nombres;
    public String apellidos;
    public String carrera;
    public String imagePath; // Ruta de la foto
}
