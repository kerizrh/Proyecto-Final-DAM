package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expedienteenlneaues.data.entity.Materia;

import java.util.List;

@Dao
public interface MateriaDao {
    @Insert
    long insert(Materia materia);

    @Update
    void update(Materia materia);

    @Delete
    void delete(Materia materia);

    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    List<Materia> getAll();

    @Query("SELECT * FROM materias WHERE carreraId = :carreraId ORDER BY nombre ASC")
    List<Materia> getByCarrera(int carreraId);

    @Query("SELECT * FROM materias WHERE id = :id")
    Materia getById(int id);

    @Query("SELECT COUNT(*) FROM materias WHERE carreraId = :carreraId")
    int getCountByCarrera(int carreraId);

    @Query("SELECT COUNT(*) FROM materias")
    int getCount();
}
