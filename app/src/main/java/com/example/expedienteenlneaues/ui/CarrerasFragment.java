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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.AppDatabase;
import com.example.expedienteenlneaues.data.entity.Carrera;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarrerasFragment extends Fragment implements CarreraAdapter.OnCarreraClickListener {

    private CarreraAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private RecyclerView recyclerView;
    private android.widget.TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = view.findViewById(R.id.recyclerViewCarreras);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CarreraAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddCarrera);
        fab.setOnClickListener(v -> showCarreraDialog(null));

        loadCarreras();

        return view;
    }

    private void loadCarreras() {
        executorService.execute(() -> {
            List<Carrera> carreras = db.carreraDao().getAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.setCarreras(carreras);
                    if (carreras.isEmpty()) {
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

    private void showCarreraDialog(Carrera carreraToEdit) {
        CarreraFormDialog dialog = new CarreraFormDialog(carreraToEdit, carrera -> {
            executorService.execute(() -> {
                if (carreraToEdit == null) {
                    db.carreraDao().insert(carrera);
                } else {
                    db.carreraDao().update(carrera);
                }
                loadCarreras();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), "Carrera guardada", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
        dialog.show(getChildFragmentManager(), "CarreraFormDialog");
    }

    @Override
    public void onCarreraClick(Carrera carrera) {
        Bundle bundle = new Bundle();
        bundle.putInt("carreraId", carrera.id);
        bundle.putString("carreraNombre", carrera.nombre);
        Navigation.findNavController(requireView()).navigate(R.id.nav_materias, bundle);
    }

    @Override
    public void onEditClick(Carrera carrera) {
        showCarreraDialog(carrera);
    }

    @Override
    public void onDeleteClick(Carrera carrera) {
        executorService.execute(() -> {
            int materiasCount = db.materiaDao().getCountByCarrera(carrera.id);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (materiasCount > 0) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("No se puede eliminar")
                                .setMessage("La carrera '" + carrera.nombre + "' tiene " + materiasCount + " materias asociadas. Debe eliminarlas antes de poder borrar la carrera.")
                                .setPositiveButton("Entendido", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar Carrera")
                                .setMessage("¿Está seguro de que desea eliminar la carrera '" + carrera.nombre + "'?")
                                .setPositiveButton("Eliminar", (dialog, which) -> {
                                    executorService.execute(() -> {
                                        db.carreraDao().delete(carrera);
                                        loadCarreras();
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(() ->
                                                    Toast.makeText(getContext(), "Carrera eliminada", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                    });
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                });
            }
        });
    }
}
