package com.example.expedienteenlneaues.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "asignaciones",
        foreignKeys = {
                @ForeignKey(entity = Docente.class,
                        parentColumns = "id",
                        childColumns = "docenteId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Materia.class,
                        parentColumns = "id",
                        childColumns = "materiaId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"docenteId", "materiaId"}, unique = true)}
)
public class Asignacion {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int docenteId;
    public int materiaId;
}
