package com.example.expedienteenlneaues.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Carrera;

public class CarreraFormDialog extends DialogFragment {

    private Carrera carrera;
    private OnSaveCarreraListener listener;

    public interface OnSaveCarreraListener {
        void onSaveCarrera(Carrera carrera);
    }

    public CarreraFormDialog(Carrera carrera, OnSaveCarreraListener listener) {
        this.carrera = carrera;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_carrera_form, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitleCarrera);
        EditText etNombre = view.findViewById(R.id.etCarreraNombre);
        EditText etFacultad = view.findViewById(R.id.etCarreraFacultad);
        Button btnCancel = view.findViewById(R.id.btnCancelCarrera);
        Button btnSave = view.findViewById(R.id.btnSaveCarrera);

        if (carrera != null) {
            tvTitle.setText("Editar Carrera");
            etNombre.setText(carrera.nombre);
            etFacultad.setText(carrera.facultad);
        } else {
            tvTitle.setText("Añadir Carrera");
        }

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String facultad = etFacultad.getText().toString().trim();

            if (nombre.isEmpty() || facultad.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (carrera == null) {
                carrera = new Carrera();
            }
            carrera.nombre = nombre;
            carrera.facultad = facultad;

            listener.onSaveCarrera(carrera);
            dismiss();
        });

        return view;
    }
}
