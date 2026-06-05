package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expedienteenlneaues.data.entity.Carrera;

import java.util.List;

@Dao
public interface CarreraDao {
    @Insert
    long insert(Carrera carrera);

    @Update
    void update(Carrera carrera);

    @Delete
    void delete(Carrera carrera);

    @Query("SELECT * FROM carreras ORDER BY nombre ASC")
    List<Carrera> getAll();

    @Query("SELECT * FROM carreras WHERE id = :id")
    Carrera getById(int id);
}
