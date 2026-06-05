package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expedienteenlneaues.data.entity.Inscripcion;
import com.example.expedienteenlneaues.data.entity.Materia;

import java.util.List;

@Dao
public interface InscripcionDao {
    @Insert
    long insert(Inscripcion inscripcion);

    @Delete
    void delete(Inscripcion inscripcion);

    // Consulta relacional usando INNER JOIN para obtener las materias de un estudiante
    @Query("SELECT m.* FROM materias m INNER JOIN inscripciones i ON m.id = i.materiaId WHERE i.expedienteId = :expedienteId")
    List<Materia> getMateriasByExpediente(int expedienteId);
}
