package com.example.expedienteenlneaues.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private AppDatabase db;
    private ExecutorService executorService;
    private TextView tvCountExpedientes, tvCountDocentes, tvCountMaterias;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = AppDatabase.getDatabase(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        tvCountExpedientes = view.findViewById(R.id.tvCountExpedientes);
        tvCountDocentes = view.findViewById(R.id.tvCountDocentes);
        tvCountMaterias = view.findViewById(R.id.tvCountMaterias);

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        executorService.execute(() -> {
            int countExpedientes = db.expedienteDao().getCount();
            int countDocentes = db.docenteDao().getCount();
            int countMaterias = db.materiaDao().getCount();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvCountExpedientes.setText(String.valueOf(countExpedientes));
                    tvCountDocentes.setText(String.valueOf(countDocentes));
                    tvCountMaterias.setText(String.valueOf(countMaterias));
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
