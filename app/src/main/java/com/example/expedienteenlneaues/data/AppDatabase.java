package com.example.expedienteenlneaues.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expedienteenlneaues.data.dao.AsignacionDao;
import com.example.expedienteenlneaues.data.dao.CarreraDao;
import com.example.expedienteenlneaues.data.dao.DocenteDao;
import com.example.expedienteenlneaues.data.dao.ExpedienteDao;
import com.example.expedienteenlneaues.data.dao.InscripcionDao;
import com.example.expedienteenlneaues.data.dao.MateriaDao;
import com.example.expedienteenlneaues.data.dao.UsuarioDao;
import com.example.expedienteenlneaues.data.entity.Asignacion;
import com.example.expedienteenlneaues.data.entity.Carrera;
import com.example.expedienteenlneaues.data.entity.Docente;
import com.example.expedienteenlneaues.data.entity.Expediente;
import com.example.expedienteenlneaues.data.entity.Inscripcion;
import com.example.expedienteenlneaues.data.entity.Materia;
import com.example.expedienteenlneaues.data.entity.Usuario;

@Database(entities = {Usuario.class, Carrera.class, Materia.class, Expediente.class, Docente.class, Inscripcion.class, Asignacion.class}, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UsuarioDao usuarioDao();
    public abstract CarreraDao carreraDao();
    public abstract MateriaDao materiaDao();
    public abstract ExpedienteDao expedienteDao();
    public abstract DocenteDao docenteDao();
    public abstract InscripcionDao inscripcionDao();
    public abstract AsignacionDao asignacionDao();

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
