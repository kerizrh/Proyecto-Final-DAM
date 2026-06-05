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
import com.example.expedienteenlneaues.data.entity.Materia;

import java.util.ArrayList;
import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    private List<Materia> materias = new ArrayList<>();
    private final OnMateriaClickListener listener;

    public interface OnMateriaClickListener {
        void onEditClick(Materia materia);
        void onDeleteClick(Materia materia);
    }

    public MateriaAdapter(OnMateriaClickListener listener) {
        this.listener = listener;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = materias.get(position);
        holder.tvMateriaNombre.setText(materia.nombre);
        holder.tvMateriaCodigo.setText(materia.codigo);
        holder.tvMateriaUVs.setText("UV: " + materia.unidadesValorativas);

        if (materia.imagePath != null && !materia.imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(materia.imagePath)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivMateriaLogo);
        } else {
            holder.ivMateriaLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnEditMateria.setOnClickListener(v -> listener.onEditClick(materia));
        holder.btnDeleteMateria.setOnClickListener(v -> listener.onDeleteClick(materia));
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }

    static class MateriaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMateriaLogo;
        TextView tvMateriaNombre, tvMateriaCodigo, tvMateriaUVs;
        ImageButton btnEditMateria, btnDeleteMateria;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMateriaLogo = itemView.findViewById(R.id.ivMateriaLogo);
            tvMateriaNombre = itemView.findViewById(R.id.tvMateriaNombre);
            tvMateriaCodigo = itemView.findViewById(R.id.tvMateriaCodigo);
            tvMateriaUVs = itemView.findViewById(R.id.tvMateriaUVs);
            btnEditMateria = itemView.findViewById(R.id.btnEditMateria);
            btnDeleteMateria = itemView.findViewById(R.id.btnDeleteMateria);
        }
    }
}
