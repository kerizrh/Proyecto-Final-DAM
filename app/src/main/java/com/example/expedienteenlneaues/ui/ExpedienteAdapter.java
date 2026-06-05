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
import com.example.expedienteenlneaues.data.entity.Expediente;

import java.util.ArrayList;
import java.util.List;

public class ExpedienteAdapter extends RecyclerView.Adapter<ExpedienteAdapter.ExpedienteViewHolder> {

    private List<Expediente> expedientes = new ArrayList<>();
    private final OnExpedienteClickListener listener;

    public interface OnExpedienteClickListener {
        void onEditClick(Expediente expediente);
        void onDeleteClick(Expediente expediente);
        void onInscribirClick(Expediente expediente);
        void onVerMateriasClick(Expediente expediente);
    }

    public ExpedienteAdapter(OnExpedienteClickListener listener) {
        this.listener = listener;
    }

    public void setExpedientes(List<Expediente> expedientes) {
        this.expedientes = expedientes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpedienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expediente, parent, false);
        return new ExpedienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpedienteViewHolder holder, int position) {
        Expediente expediente = expedientes.get(position);
        holder.tvExpedienteNombres.setText(expediente.nombres + " " + expediente.apellidos);
        holder.tvExpedienteCarnet.setText("Carnet: " + expediente.carnet);
        holder.tvExpedienteCarrera.setText(expediente.carrera);

        if (expediente.imagePath != null && !expediente.imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(expediente.imagePath)
                    .placeholder(R.drawable.ic_folder)
                    .error(R.drawable.ic_folder)
                    .into(holder.ivExpedienteFoto);
        } else {
            holder.ivExpedienteFoto.setImageResource(R.drawable.ic_folder);
        }

        holder.btnEditExpediente.setOnClickListener(v -> listener.onEditClick(expediente));
        holder.btnDeleteExpediente.setOnClickListener(v -> listener.onDeleteClick(expediente));
        holder.btnInscribirMateria.setOnClickListener(v -> listener.onInscribirClick(expediente));
        holder.btnVerMaterias.setOnClickListener(v -> listener.onVerMateriasClick(expediente));
    }

    @Override
    public int getItemCount() {
        return expedientes.size();
    }

    static class ExpedienteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExpedienteFoto;
        TextView tvExpedienteNombres, tvExpedienteCarnet, tvExpedienteCarrera;
        ImageButton btnEditExpediente, btnDeleteExpediente, btnInscribirMateria, btnVerMaterias;

        public ExpedienteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExpedienteFoto = itemView.findViewById(R.id.ivExpedienteFoto);
            tvExpedienteNombres = itemView.findViewById(R.id.tvExpedienteNombres);
            tvExpedienteCarnet = itemView.findViewById(R.id.tvExpedienteCarnet);
            tvExpedienteCarrera = itemView.findViewById(R.id.tvExpedienteCarrera);
            btnEditExpediente = itemView.findViewById(R.id.btnEditExpediente);
            btnDeleteExpediente = itemView.findViewById(R.id.btnDeleteExpediente);
            btnInscribirMateria = itemView.findViewById(R.id.btnInscribirMateria);
            btnVerMaterias = itemView.findViewById(R.id.btnVerMaterias);
        }
    }
}
