package com.example.expedienteenlneaues.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Docente;

import java.util.ArrayList;
import java.util.List;

public class DocenteAdapter extends RecyclerView.Adapter<DocenteAdapter.DocenteViewHolder> {

    private List<Docente> docentes = new ArrayList<>();
    private final OnDocenteClickListener listener;

    public interface OnDocenteClickListener {
        void onEditClick(Docente docente);
        void onDeleteClick(Docente docente);
        void onAsignarClick(Docente docente);
        void onVerCargaClick(Docente docente);
    }

    public DocenteAdapter(OnDocenteClickListener listener) {
        this.listener = listener;
    }

    public void setDocentes(List<Docente> docentes) {
        this.docentes = docentes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_docente, parent, false);
        return new DocenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocenteViewHolder holder, int position) {
        Docente docente = docentes.get(position);
        holder.tvDocenteNombres.setText(docente.nombres + " " + docente.apellidos);
        holder.tvDocenteEscalafon.setText("Escalafón/DUI: " + docente.escalafon);
        holder.tvDocenteEspecialidad.setText(docente.especialidad);

        if (docente.imagePath != null && !docente.imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(docente.imagePath)
                    .placeholder(R.drawable.ic_badge)
                    .error(R.drawable.ic_badge)
                    .into(holder.ivDocenteFoto);
        } else {
            holder.ivDocenteFoto.setImageResource(R.drawable.ic_badge);
        }

        holder.btnEditDocente.setOnClickListener(v -> listener.onEditClick(docente));
        holder.btnDeleteDocente.setOnClickListener(v -> listener.onDeleteClick(docente));
        holder.btnAsignarMateria.setOnClickListener(v -> listener.onAsignarClick(docente));
        holder.btnVerCarga.setOnClickListener(v -> listener.onVerCargaClick(docente));
    }

    @Override
    public int getItemCount() {
        return docentes.size();
    }

    static class DocenteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDocenteFoto;
        TextView tvDocenteNombres, tvDocenteEscalafon, tvDocenteEspecialidad;
        ImageButton btnEditDocente, btnDeleteDocente, btnAsignarMateria, btnVerCarga;

        public DocenteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDocenteFoto = itemView.findViewById(R.id.ivDocenteFoto);
            tvDocenteNombres = itemView.findViewById(R.id.tvDocenteNombres);
            tvDocenteEscalafon = itemView.findViewById(R.id.tvDocenteEscalafon);
            tvDocenteEspecialidad = itemView.findViewById(R.id.tvDocenteEspecialidad);
            btnEditDocente = itemView.findViewById(R.id.btnEditDocente);
            btnDeleteDocente = itemView.findViewById(R.id.btnDeleteDocente);
            btnAsignarMateria = itemView.findViewById(R.id.btnAsignarMateria);
            btnVerCarga = itemView.findViewById(R.id.btnVerCarga);
        }
    }
}
