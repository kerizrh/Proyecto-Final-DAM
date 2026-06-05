package com.example.expedienteenlneaues.ui;

import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Docente;

public class DocenteFormDialog extends DialogFragment {

    private Docente docente;
    private OnSaveDocenteListener listener;
    private String selectedImagePath = "";
    private ActivityResultLauncher<String[]> photoPickerLauncher;

    public interface OnSaveDocenteListener {
        void onSaveDocente(Docente docente);
    }

    public DocenteFormDialog(Docente docente, OnSaveDocenteListener listener) {
        this.docente = docente;
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
                            TextView tvSelectedImage = getView().findViewById(R.id.tvSelectedImageDocente);
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
        View view = inflater.inflate(R.layout.dialog_docente_form, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitleDocente);
        EditText etEscalafon = view.findViewById(R.id.etEscalafon);
        EditText etNombres = view.findViewById(R.id.etDocenteNombres);
        EditText etApellidos = view.findViewById(R.id.etDocenteApellidos);
        EditText etEspecialidad = view.findViewById(R.id.etDocenteEspecialidad);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImageDocente);
        TextView tvSelectedImage = view.findViewById(R.id.tvSelectedImageDocente);
        Button btnCancel = view.findViewById(R.id.btnCancelDocente);
        Button btnSave = view.findViewById(R.id.btnSaveDocente);

        if (docente != null) {
            tvTitle.setText("Editar Docente");
            etEscalafon.setText(docente.escalafon);
            etNombres.setText(docente.nombres);
            etApellidos.setText(docente.apellidos);
            etEspecialidad.setText(docente.especialidad);
            if (docente.imagePath != null && !docente.imagePath.isEmpty()) {
                selectedImagePath = docente.imagePath;
                tvSelectedImage.setText("Foto adjunta ✓");
            }
        } else {
            tvTitle.setText("Añadir Docente");
        }

        btnSelectImage.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String escalafon = etEscalafon.getText().toString().trim();
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String especialidad = etEspecialidad.getText().toString().trim();

            if (escalafon.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || especialidad.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos de texto son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (docente == null) {
                docente = new Docente();
            }
            docente.escalafon = escalafon;
            docente.nombres = nombres;
            docente.apellidos = apellidos;
            docente.especialidad = especialidad;
            docente.imagePath = selectedImagePath;

            listener.onSaveDocente(docente);
            dismiss();
        });

        return view;
    }
}
