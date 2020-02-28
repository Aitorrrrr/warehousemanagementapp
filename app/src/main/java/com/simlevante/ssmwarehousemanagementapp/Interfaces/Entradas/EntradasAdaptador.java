package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textview.MaterialTextView;
import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class EntradasAdaptador extends RecyclerView.Adapter<EntradasAdaptador.EntradasViewHolder>
{
    private ArrayList<Movimiento> movCreados;
    private Context context;

    public static class EntradasViewHolder extends RecyclerView.ViewHolder
    {
        public MaterialTextView codProd;
        public MaterialTextView uds;
        public CardView cv;

        public EntradasViewHolder(@NonNull View itemView) {
            super(itemView);

            codProd = itemView.findViewById(R.id.viewholder_entradas_codProd);
            uds = itemView.findViewById(R.id.viewholder_entradas_uds);
            cv = itemView.findViewById(R.id.viewholder_entradas_cardView);
        }
    }

    public EntradasAdaptador(ArrayList<Movimiento> movCreados, Context context)
    {
        this.movCreados = movCreados;
        this.context = context;
    }

    public void setArray(ArrayList<Movimiento> movimientos)
    {
        this.movCreados = movimientos;
        Log.d("Aitor", Integer.toString(movCreados.size()));
    }

    @NonNull
    @Override
    public EntradasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_entradas_movimientos,viewGroup, false);

        return new EntradasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EntradasViewHolder holder, int i)
    {
        Movimiento movAux = new Movimiento(movCreados.get(i));

        holder.codProd.setText(movAux.getCodart());
        holder.uds.setText(Double.toString(movAux.getUnidades()));

        if (movAux.getMoproviene() != null)
        {
            if (movAux.getMoproviene().trim().compareTo("") == 0)
            {
                holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.amarilloBebe, null));
            }
        }
    }

    @Override
    public int getItemCount() {
        return movCreados.size();
    }
}
