package com.example.expedienteenlneaues.ui;

import android.app.Dialog;
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
import com.example.expedienteenlneaues.data.entity.Materia;

public class MateriaFormDialog extends DialogFragment {

    private Materia materia;
    private OnSaveMateriaListener listener;
    private String selectedImagePath = "";
    private ActivityResultLauncher<String[]> photoPickerLauncher;

    public interface OnSaveMateriaListener {
        void onSaveMateria(Materia materia);
    }

    public MateriaFormDialog(Materia materia, OnSaveMateriaListener listener) {
        this.materia = materia;
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
                            TextView tvSelectedImage = getView().findViewById(R.id.tvSelectedImageMateria);
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
        View view = inflater.inflate(R.layout.dialog_materia_form, container, false);
        
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etCodigo = view.findViewById(R.id.etCodigo);
        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etUVs = view.findViewById(R.id.etUVs);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImageMateria);
        TextView tvSelectedImage = view.findViewById(R.id.tvSelectedImageMateria);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        if (materia != null) {
            tvTitle.setText("Editar Materia");
            etCodigo.setText(materia.codigo);
            etNombre.setText(materia.nombre);
            etUVs.setText(String.valueOf(materia.unidadesValorativas));
            if (materia.imagePath != null && !materia.imagePath.isEmpty()) {
                selectedImagePath = materia.imagePath;
                tvSelectedImage.setText("Foto adjunta ✓");
            }
        } else {
            tvTitle.setText("Añadir Materia");
        }

        btnSelectImage.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String uvsStr = etUVs.getText().toString().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || uvsStr.isEmpty()) {
                Toast.makeText(getContext(), "Código, Nombre y UVs son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int uvs;
            try {
                uvs = Integer.parseInt(uvsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "UVs debe ser un número", Toast.LENGTH_SHORT).show();
                return;
            }

            if (materia == null) {
                materia = new Materia();
            }
            materia.codigo = codigo;
            materia.nombre = nombre;
            materia.unidadesValorativas = uvs;
            materia.imagePath = selectedImagePath;

            listener.onSaveMateria(materia);
            dismiss();
        });

        return view;
    }
}
