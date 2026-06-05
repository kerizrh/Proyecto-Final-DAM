package com.example.expedienteenlneaues.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expedienteenlneaues.data.entity.Usuario;

@Dao
public interface UsuarioDao {
    @Insert
    long insert(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE username = :username AND password = :password LIMIT 1")
    Usuario login(String username, String password);
    
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    Usuario getByUsername(String username);
}
