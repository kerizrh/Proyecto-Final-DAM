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
    private RecyclerView recyclerView;
    private android.widget.TextView tvEmptyState;
    private int carreraId = -1;
    private String carreraNombre = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        if (getArguments() != null) {
            carreraId = getArguments().getInt("carreraId", -1);
            carreraNombre = getArguments().getString("carreraNombre", "");
        }

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = view.findViewById(R.id.recyclerViewMaterias);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MateriaAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddMateria);
        fab.setOnClickListener(v -> showMateriaDialog(null));

        loadMaterias();

        return view;
    }

    private void loadMaterias() {
        if (carreraId == -1) return;
        executorService.execute(() -> {
            List<Materia> materias = db.materiaDao().getByCarrera(carreraId);
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    adapter.setMaterias(materias);
                    if (materias.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void showMateriaDialog(Materia materiaToEdit) {
        MateriaFormDialog dialog = new MateriaFormDialog(materiaToEdit, carreraId, materia -> {
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
