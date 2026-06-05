package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "carreras")
public class Carrera {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String codigo;
    public String nombre;
    public String facultad;
    public String fotoPath;
}
