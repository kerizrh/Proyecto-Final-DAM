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
import com.example.expedienteenlneaues.data.entity.Materia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MateriasFragment extends Fragment implements MateriaAdapter.OnMateriaClickListener {

    private MateriaAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MateriaAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddMateria);
        fab.setOnClickListener(v -> showMateriaDialog(null));

        loadMaterias();

        return view;
    }

    private void loadMaterias() {
        executorService.execute(() -> {
            List<Materia> materias = db.materiaDao().getAll();
            requireActivity().runOnUiThread(() -> adapter.setMaterias(materias));
        });
    }

    private void showMateriaDialog(Materia materiaToEdit) {
        MateriaFormDialog dialog = new MateriaFormDialog(materiaToEdit, materia -> {
            executorService.execute(() -> {
                if (materiaToEdit == null) {
                    db.materiaDao().insert(materia);
                } else {
                    db.materiaDao().update(materia);
                }
                loadMaterias();
                requireActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                );
            });
        });
        dialog.show(getChildFragmentManager(), "MateriaFormDialog");
    }

    @Override
    public void onEditClick(Materia materia) {
        showMateriaDialog(materia);
    }

    @Override
    public void onDeleteClick(Materia materia) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Materia")
                .setMessage("¿Estás seguro de que deseas eliminar " + materia.nombre + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    executorService.execute(() -> {
                        db.materiaDao().delete(materia);
                        loadMaterias();
                        requireActivity().runOnUiThread(() -> 
                                Toast.makeText(getContext(), "Materia eliminada", Toast.LENGTH_SHORT).show()
                        );
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
