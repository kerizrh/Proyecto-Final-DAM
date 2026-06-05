package com.example.expedienteenlneaues.ui;

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
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Expediente;
import com.example.expedienteenlneaues.data.entity.Carrera;

import java.util.List;
import java.util.ArrayList;

public class ExpedienteFormDialog extends DialogFragment {

    private Expediente expediente;
    private List<Carrera> carreras;
    private OnSaveExpedienteListener listener;
    private String selectedImagePath = "";
    private ActivityResultLauncher<String[]> photoPickerLauncher;

    public interface OnSaveExpedienteListener {
        void onSaveExpediente(Expediente expediente);
    }

    public ExpedienteFormDialog(Expediente expediente, List<Carrera> carreras, OnSaveExpedienteListener listener) {
        this.expediente = expediente;
        this.carreras = carreras;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            requireActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        selectedImagePath = uri.toString();
                        if (getView() != null) {
                            TextView tvSelectedImage = getView().findViewById(R.id.tvSelectedImageExpediente);
                            if (tvSelectedImage != null) {
                                tvSelectedImage.setText("Foto adjunta ✓");
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_expediente_form, container, false);
        
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitleExpediente);
        EditText etCarnet = view.findViewById(R.id.etCarnet);
        EditText etNombres = view.findViewById(R.id.etNombres);
        EditText etApellidos = view.findViewById(R.id.etApellidos);
        Spinner spCarrera = view.findViewById(R.id.spCarrera);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImageExpediente);
        TextView tvSelectedImage = view.findViewById(R.id.tvSelectedImageExpediente);
        Button btnCancel = view.findViewById(R.id.btnCancelExpediente);
        Button btnSave = view.findViewById(R.id.btnSaveExpediente);

        List<String> nombresCarreras = new ArrayList<>();
        for (Carrera c : carreras) {
            nombresCarreras.add(c.nombre);
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresCarreras);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCarrera.setAdapter(spinnerAdapter);

        if (expediente != null) {
            tvTitle.setText("Editar Expediente");
            etCarnet.setText(expediente.carnet);
            etNombres.setText(expediente.nombres);
            etApellidos.setText(expediente.apellidos);
            
            for (int i = 0; i < carreras.size(); i++) {
                if (carreras.get(i).id == expediente.carreraId) {
                    spCarrera.setSelection(i);
                    break;
                }
            }

            if (expediente.fotoPath != null && !expediente.fotoPath.isEmpty()) {
                selectedImagePath = expediente.fotoPath;
                tvSelectedImage.setText("Foto adjunta ✓");
            }
        } else {
            tvTitle.setText("Añadir Expediente");
        }

        btnSelectImage.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String carnet = etCarnet.getText().toString().trim();
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            
            if (carreras.isEmpty()) {
                Toast.makeText(getContext(), "Debe existir al menos una carrera para matricular a un estudiante", Toast.LENGTH_SHORT).show();
                return;
            }

            if (carnet.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos de texto son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (expediente == null) {
                expediente = new Expediente();
            }
            expediente.carnet = carnet;
            expediente.nombres = nombres;
            expediente.apellidos = apellidos;
            expediente.carreraId = carreras.get(spCarrera.getSelectedItemPosition()).id;
            expediente.fotoPath = selectedImagePath;

            listener.onSaveExpediente(expediente);
            dismiss();
        });

        return view;
    }
}
