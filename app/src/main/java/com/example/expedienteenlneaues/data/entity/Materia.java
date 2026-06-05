package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "materias")
public class Materia {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String codigo;
    public String nombre;
    public int unidadesValorativas;
    public String imagePath; // Ruta del logo de la facultad o materia
}
