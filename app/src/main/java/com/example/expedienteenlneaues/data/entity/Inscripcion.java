package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "inscripciones",
        foreignKeys = {
                @ForeignKey(entity = Expediente.class,
                        parentColumns = "id",
                        childColumns = "expedienteId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Materia.class,
                        parentColumns = "id",
                        childColumns = "materiaId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"expedienteId", "materiaId"}, unique = true)}
)
public class Inscripcion {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int expedienteId;
    public int materiaId;
}
