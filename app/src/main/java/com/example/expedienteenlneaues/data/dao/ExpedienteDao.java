package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expedienteenlneaues.data.entity.Expediente;

import java.util.List;

@Dao
public interface ExpedienteDao {
    @Insert
    long insert(Expediente expediente);

    @Update
    void update(Expediente expediente);

    @Delete
    void delete(Expediente expediente);

    @Query("SELECT * FROM expedientes ORDER BY apellidos ASC")
    List<Expediente> getAll();

    @Query("SELECT * FROM expedientes WHERE id = :id")
    Expediente getById(int id);

    @Query("SELECT COUNT(*) FROM expedientes")
    int getCount();
}
