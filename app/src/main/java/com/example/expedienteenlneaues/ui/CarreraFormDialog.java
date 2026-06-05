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
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Carrera;

public class CarreraFormDialog extends DialogFragment {

    private Carrera carrera;
    private OnSaveCarreraListener listener;
    private String selectedImagePath = "";
    private ActivityResultLauncher<String[]> photoPickerLauncher;

    public interface OnSaveCarreraListener {
        void onSaveCarrera(Carrera carrera);
    }

    public CarreraFormDialog(Carrera carrera, OnSaveCarreraListener listener) {
        this.carrera = carrera;
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
                            TextView tvSelectedImage = getView().findViewById(R.id.tvSelectedImageCarrera);
                            if (tvSelectedImage != null) {
                                tvSelectedImage.setText("Logo adjunto ✓");
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_carrera_form, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitleCarrera);
        EditText etCodigo = view.findViewById(R.id.etCarreraCodigo);
        EditText etNombre = view.findViewById(R.id.etCarreraNombre);
        EditText etFacultad = view.findViewById(R.id.etCarreraFacultad);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImageCarrera);
        TextView tvSelectedImage = view.findViewById(R.id.tvSelectedImageCarrera);
        Button btnCancel = view.findViewById(R.id.btnCancelCarrera);
        Button btnSave = view.findViewById(R.id.btnSaveCarrera);

        if (carrera != null) {
            tvTitle.setText("Editar Carrera");
            etCodigo.setText(carrera.codigo);
            etNombre.setText(carrera.nombre);
            etFacultad.setText(carrera.facultad);
            if (carrera.fotoPath != null && !carrera.fotoPath.isEmpty()) {
                selectedImagePath = carrera.fotoPath;
                tvSelectedImage.setText("Logo adjunto ✓");
            }
        } else {
            tvTitle.setText("Añadir Carrera");
        }

        btnSelectImage.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String facultad = etFacultad.getText().toString().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || facultad.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (carrera == null) {
                carrera = new Carrera();
            }
            carrera.codigo = codigo;
            carrera.nombre = nombre;
            carrera.facultad = facultad;
            carrera.fotoPath = selectedImagePath;

            listener.onSaveCarrera(carrera);
            dismiss();
        });

        return view;
    }
}
