package com.example.expedienteenlneaues.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expedienteenlneaues.data.dao.ArticuloDao;
import com.example.expedienteenlneaues.data.dao.UsuarioDao;
import com.example.expedienteenlneaues.data.entity.Articulo;
import com.example.expedienteenlneaues.data.entity.Usuario;

@Database(entities = {Usuario.class, Articulo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UsuarioDao usuarioDao();
    public abstract ArticuloDao articuloDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "expediente_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
