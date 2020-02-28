package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.ArrayList;
import java.util.List;

public class PendientesFragment extends Fragment {

    private static final String ARG_PARAM1 = "movPdtes";
    private ArrayList<Movimiento> movPdtes;

    private RecyclerView recycler_Pdtes;
    private PendientesAdaptador adaptadorRecycler;
    private RecyclerView.LayoutManager rvLM;

    public PendientesFragment() {

    }

    public static PendientesFragment newInstance(ArrayList<Movimiento> movPdtes) {
        PendientesFragment fragment = new PendientesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, movPdtes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movPdtes = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pendientes, container, false);
        recycler_Pdtes = v.findViewById(R.id.pendientes_rv);
        rvLM = new LinearLayoutManager(v.getContext());
        recycler_Pdtes.setLayoutManager(rvLM);
        adaptadorRecycler = new PendientesAdaptador(movPdtes);
        recycler_Pdtes.setAdapter(adaptadorRecycler);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
