package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PendientesAdaptador extends RecyclerView.Adapter<PendientesAdaptador.PendientesViewHolder>
{
    private ArrayList<Movimiento> movPdtes;

    public static class PendientesViewHolder extends RecyclerView.ViewHolder
    {
        public MaterialTextView codBar;
        public MaterialTextView descripcion;
        public MaterialTextView uds;
        public MaterialTextView incorp;

        public PendientesViewHolder(@NonNull View itemView) {
            super(itemView);

            codBar = itemView.findViewById(R.id.viewholder_pendientes_codBar);
            descripcion = itemView.findViewById(R.id.viewholder_pendientes_descripcion);
            uds = itemView.findViewById(R.id.viewholder_pendientes_uds);
            incorp = itemView.findViewById(R.id.viewholder_pendientes_incorp);
        }
    }

    public PendientesAdaptador(ArrayList<Movimiento> movPdtes)
    {
        this.movPdtes = movPdtes;
    }

    @NonNull
    @Override
    public PendientesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_mov_pendientes,viewGroup, false);

        return new PendientesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PendientesViewHolder holder, int i)
    {
        holder.codBar.setText(movPdtes.get(i).getEan13());
        holder.descripcion.setText(movPdtes.get(i).getDenominaci());
        holder.uds.setText(Double.toString(movPdtes.get(i).getUnidades()));
        holder.incorp.setText(Double.toString(movPdtes.get(i).getIncorporad()));
    }

    @Override
    public int getItemCount() {
        return movPdtes.size();
    }
}
