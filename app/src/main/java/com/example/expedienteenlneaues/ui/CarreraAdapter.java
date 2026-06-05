package com.example.expedienteenlneaues.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expedienteenlneaues.R;
import com.example.expedienteenlneaues.data.entity.Carrera;

import java.util.ArrayList;
import java.util.List;

public class CarreraAdapter extends RecyclerView.Adapter<CarreraAdapter.CarreraViewHolder> {

    private List<Carrera> carreras = new ArrayList<>();
    private final OnCarreraClickListener listener;

    public interface OnCarreraClickListener {
        void onCarreraClick(Carrera carrera);
        void onEditClick(Carrera carrera);
        void onDeleteClick(Carrera carrera);
    }

    public CarreraAdapter(OnCarreraClickListener listener) {
        this.listener = listener;
    }

    public void setCarreras(List<Carrera> carreras) {
        this.carreras = carreras;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarreraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrera, parent, false);
        return new CarreraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarreraViewHolder holder, int position) {
        Carrera carrera = carreras.get(position);
        if (carrera.codigo != null && !carrera.codigo.isEmpty()) {
            holder.tvCarreraNombre.setText(carrera.nombre + " (" + carrera.codigo + ")");
        } else {
            holder.tvCarreraNombre.setText(carrera.nombre);
        }
        holder.tvCarreraFacultad.setText(carrera.facultad);

        if (carrera.fotoPath != null && !carrera.fotoPath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(carrera.fotoPath)
                    .placeholder(R.drawable.ic_university)
                    .error(R.drawable.ic_university)
                    .into(holder.ivCarreraFoto);
        } else {
            holder.ivCarreraFoto.setImageResource(R.drawable.ic_university);
        }

        holder.itemView.setOnClickListener(v -> listener.onCarreraClick(carrera));
        holder.btnEditCarrera.setOnClickListener(v -> listener.onEditClick(carrera));
        holder.btnDeleteCarrera.setOnClickListener(v -> listener.onDeleteClick(carrera));
    }

    @Override
    public int getItemCount() {
        return carreras.size();
    }

    static class CarreraViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCarreraFoto;
        TextView tvCarreraNombre, tvCarreraFacultad;
        ImageButton btnEditCarrera, btnDeleteCarrera;

        public CarreraViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarreraFoto = itemView.findViewById(R.id.ivCarreraFoto);
            tvCarreraNombre = itemView.findViewById(R.id.tvCarreraNombre);
            tvCarreraFacultad = itemView.findViewById(R.id.tvCarreraFacultad);
            btnEditCarrera = itemView.findViewById(R.id.btnEditCarrera);
            btnDeleteCarrera = itemView.findViewById(R.id.btnDeleteCarrera);
        }
    }
}
