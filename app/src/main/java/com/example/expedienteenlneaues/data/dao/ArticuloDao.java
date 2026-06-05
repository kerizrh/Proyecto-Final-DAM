package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expedienteenlneaues.data.entity.Articulo;

import java.util.List;

@Dao
public interface ArticuloDao {
    @Insert
    long insert(Articulo articulo);

    @Update
    void update(Articulo articulo);

    @Delete
    void delete(Articulo articulo);

    @Query("SELECT * FROM articulos ORDER BY nombre ASC")
    List<Articulo> getAll();

    @Query("SELECT * FROM articulos WHERE id = :id")
    Articulo getById(int id);
}
