package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "materias",
        foreignKeys = @ForeignKey(entity = Carrera.class,
                parentColumns = "id",
                childColumns = "carreraId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("carreraId")})
public class Materia {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String codigo;
    public int unidadesValorativas;
    public String imagePath;
    public int carreraId;
}
