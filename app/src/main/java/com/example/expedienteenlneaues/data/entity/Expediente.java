package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "expedientes",
        foreignKeys = @ForeignKey(entity = Carrera.class,
                parentColumns = "id",
                childColumns = "carreraId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("carreraId")})
public class Expediente {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String carnet;
    public String nombres;
    public String apellidos;
    public String fotoPath;
    public int carreraId;
}
