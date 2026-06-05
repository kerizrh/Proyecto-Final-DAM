package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expedienteenlneaues.data.entity.Asignacion;
import com.example.expedienteenlneaues.data.entity.Materia;

import java.util.List;

@Dao
public interface AsignacionDao {
    @Insert
    long insert(Asignacion asignacion);

    @Delete
    void delete(Asignacion asignacion);

    // Consulta relacional usando INNER JOIN para obtener la carga académica de un docente
    @Query("SELECT m.* FROM materias m INNER JOIN asignaciones a ON m.id = a.materiaId WHERE a.docenteId = :docenteId")
    List<Materia> getMateriasByDocente(int docenteId);
}
