package com.example.expedienteenlneaues.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expedienteenlneaues.data.dao.DocenteDao;
import com.example.expedienteenlneaues.data.dao.ExpedienteDao;
import com.example.expedienteenlneaues.data.dao.MateriaDao;
import com.example.expedienteenlneaues.data.dao.UsuarioDao;
import com.example.expedienteenlneaues.data.entity.Docente;
import com.example.expedienteenlneaues.data.entity.Expediente;
import com.example.expedienteenlneaues.data.entity.Materia;
import com.example.expedienteenlneaues.data.entity.Usuario;

@Database(entities = {Usuario.class, Materia.class, Expediente.class, Docente.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UsuarioDao usuarioDao();
    public abstract MateriaDao materiaDao();
    public abstract ExpedienteDao expedienteDao();
    public abstract DocenteDao docenteDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "expediente_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
