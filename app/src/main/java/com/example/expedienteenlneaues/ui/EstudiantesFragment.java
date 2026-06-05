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
import com.example.expedienteenlneaues.data.entity.Carrera;
import com.example.expedienteenlneaues.data.entity.Expediente;
import com.example.expedienteenlneaues.data.entity.Inscripcion;
import com.example.expedienteenlneaues.data.entity.Materia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EstudiantesFragment extends Fragment implements ExpedienteAdapter.OnExpedienteClickListener {

    private ExpedienteAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estudiantes, container, false);

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewExpedientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpedienteAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddExpediente);
        fab.setOnClickListener(v -> showExpedienteDialog(null));

        loadExpedientes();

        return view;
    }

    private void loadExpedientes() {
        executorService.execute(() -> {
            List<Expediente> expedientes = db.expedienteDao().getAll();
            List<Carrera> carreras = db.carreraDao().getAll();
            Map<Integer, String> carrerasMap = new HashMap<>();
            for (Carrera c : carreras) {
                carrerasMap.put(c.id, c.nombre);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setExpedientes(expedientes, carrerasMap));
            }
        });
    }

    private void showExpedienteDialog(Expediente expedienteToEdit) {
        executorService.execute(() -> {
            List<Carrera> carrerasDisponibles = db.carreraDao().getAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (carrerasDisponibles.isEmpty()) {
                        Toast.makeText(getContext(), "Crea primero una Carrera para inscribir estudiantes", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ExpedienteFormDialog dialog = new ExpedienteFormDialog(expedienteToEdit, carrerasDisponibles, expediente -> {
                        executorService.execute(() -> {
                            if (expedienteToEdit == null) {
                                db.expedienteDao().insert(expediente);
                            } else {
                                db.expedienteDao().update(expediente);
                            }
                            loadExpedientes();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Expediente guardado", Toast.LENGTH_SHORT).show());
                            }
                        });
                    });
                    dialog.show(getChildFragmentManager(), "ExpedienteFormDialog");
                });
            }
        });
    }

    @Override
    public void onEditClick(Expediente expediente) {
        showExpedienteDialog(expediente);
    }

    @Override
    public void onDeleteClick(Expediente expediente) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Expediente")
                .setMessage("¿Eliminar expediente de " + expediente.nombres + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    executorService.execute(() -> {
                        db.expedienteDao().delete(expediente);
                        loadExpedientes();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> 
                                    Toast.makeText(getContext(), "Expediente eliminado", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onInscribirClick(Expediente expediente) {
        executorService.execute(() -> {
            List<Materia> materiasDisponibles = db.materiaDao().getByCarrera(expediente.carreraId);
            if (materiasDisponibles.isEmpty()) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "No hay materias en el pensum de esta carrera", Toast.LENGTH_SHORT).show()
                    );
                }
                return;
            }

            String[] nombresMaterias = new String[materiasDisponibles.size()];
            for (int i = 0; i < materiasDisponibles.size(); i++) {
                nombresMaterias[i] = materiasDisponibles.get(i).nombre + " (" + materiasDisponibles.get(i).codigo + ")";
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Inscribir Materia a " + expediente.nombres)
                            .setItems(nombresMaterias, (dialog, which) -> {
                                Materia seleccionada = materiasDisponibles.get(which);
                                executorService.execute(() -> {
                                    Inscripcion nuevaInscripcion = new Inscripcion();
                                    nuevaInscripcion.expedienteId = expediente.id;
                                    nuevaInscripcion.materiaId = seleccionada.id;
                                    db.inscripcionDao().insert(nuevaInscripcion);
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> 
                                            Toast.makeText(getContext(), "Materia inscrita: " + seleccionada.nombre, Toast.LENGTH_SHORT).show()
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
    public void onVerMateriasClick(Expediente expediente) {
        executorService.execute(() -> {
            List<Materia> materiasInscritas = db.inscripcionDao().getMateriasByExpediente(expediente.id);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (materiasInscritas.isEmpty()) {
                        Toast.makeText(getContext(), "El estudiante no tiene materias inscritas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] nombres = new String[materiasInscritas.size()];
                    for (int i = 0; i < materiasInscritas.size(); i++) {
                        nombres[i] = materiasInscritas.get(i).nombre;
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Materias Inscritas")
                            .setItems(nombres, null)
                            .setPositiveButton("Cerrar", null)
                            .show();
                });
            }
        });
    }
}
