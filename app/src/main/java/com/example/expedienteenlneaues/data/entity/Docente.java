package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "docentes")
public class Docente {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String escalafon;
    public String nombres;
    public String apellidos;
    public String especialidad;
    public String imagePath;
}
