package com.example.expedienteenlneaues.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.AppDatabase;
import com.example.expedienteenlneaues.data.entity.Asignacion;
import com.example.expedienteenlneaues.data.entity.Docente;
import com.example.expedienteenlneaues.data.entity.Materia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocentesFragment extends Fragment implements DocenteAdapter.OnDocenteClickListener {

    private DocenteAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docentes, container, false);

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDocentes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DocenteAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddDocente);
        fab.setOnClickListener(v -> showDocenteDialog(null));

        loadDocentes();

        return view;
    }

    private void loadDocentes() {
        executorService.execute(() -> {
            List<Docente> docentes = db.docenteDao().getAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setDocentes(docentes));
            }
        });
    }

    private void showDocenteDialog(Docente docenteToEdit) {
        DocenteFormDialog dialog = new DocenteFormDialog(docenteToEdit, docente -> {
            executorService.execute(() -> {
                if (docenteToEdit == null) {
                    db.docenteDao().insert(docente);
                } else {
                    db.docenteDao().update(docente);
                }
                loadDocentes();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), "Docente guardado", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
        dialog.show(getChildFragmentManager(), "DocenteFormDialog");
    }

    @Override
    public void onEditClick(Docente docente) {
        showDocenteDialog(docente);
    }

    @Override
    public void onDeleteClick(Docente docente) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Docente")
                .setMessage("¿Eliminar al docente " + docente.nombres + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    executorService.execute(() -> {
                        db.docenteDao().delete(docente);
                        loadDocentes();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> 
                                    Toast.makeText(getContext(), "Docente eliminado", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onAsignarClick(Docente docente) {
        executorService.execute(() -> {
            List<Materia> todasLasMaterias = db.materiaDao().getAll();
            if (todasLasMaterias.isEmpty()) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "No hay materias disponibles en el pensum", Toast.LENGTH_SHORT).show()
                    );
                }
                return;
            }

            String[] nombresMaterias = new String[todasLasMaterias.size()];
            for (int i = 0; i < todasLasMaterias.size(); i++) {
                nombresMaterias[i] = todasLasMaterias.get(i).nombre + " (" + todasLasMaterias.get(i).codigo + ")";
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Asignar Materia a " + docente.nombres)
                            .setItems(nombresMaterias, (dialog, which) -> {
                                Materia seleccionada = todasLasMaterias.get(which);
                                executorService.execute(() -> {
                                    Asignacion nuevaAsignacion = new Asignacion();
                                    nuevaAsignacion.docenteId = docente.id;
                                    nuevaAsignacion.materiaId = seleccionada.id;
                                    db.asignacionDao().insert(nuevaAsignacion);
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> 
                                            Toast.makeText(getContext(), "Materia asignada: " + seleccionada.nombre, Toast.LENGTH_SHORT).show()
                                        );
                                    }
                                });
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
            }
        });
    }

    @Override
    public void onVerCargaClick(Docente docente) {
        executorService.execute(() -> {
            List<Materia> materiasAsignadas = db.asignacionDao().getMateriasByDocente(docente.id);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (materiasAsignadas.isEmpty()) {
                        Toast.makeText(getContext(), "El docente no tiene materias asignadas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] nombres = new String[materiasAsignadas.size()];
                    for (int i = 0; i < materiasAsignadas.size(); i++) {
                        nombres[i] = materiasAsignadas.get(i).nombre;
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Carga Académica")
                            .setItems(nombres, null)
                            .setPositiveButton("Cerrar", null)
                            .show();
                });
            }
        });
    }
}
