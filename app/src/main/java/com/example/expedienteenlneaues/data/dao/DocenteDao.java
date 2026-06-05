package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expedienteenlneaues.data.entity.Docente;

import java.util.List;

@Dao
public interface DocenteDao {
    @Insert
    long insert(Docente docente);

    @Update
    void update(Docente docente);

    @Delete
    void delete(Docente docente);

    @Query("SELECT * FROM docentes ORDER BY apellidos ASC")
    List<Docente> getAll();

    @Query("SELECT * FROM docentes WHERE id = :id")
    Docente getById(int id);
}
