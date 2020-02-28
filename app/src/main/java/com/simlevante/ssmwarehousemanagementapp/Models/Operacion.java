package com.simlevante.ssmwarehousemanagementapp.Models;

import java.util.ArrayList;

public class Operacion
{
    private double numero;
    private double pendientes;
    private String numeroDoc;
    private ArrayList<Movimiento> movAlbaran = new ArrayList<>();

    public double getNumero() {
        return numero;
    }

    public void setNumero(double numero) {
        this.numero = numero;
    }

    public double getPendientes() {
        return pendientes;
    }

    public void setPendientes(double pendientes) {
        this.pendientes = pendientes;
    }

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public ArrayList<Movimiento> getMovAlbaran() {
        return movAlbaran;
    }

    public void setMovAlbaran(ArrayList<Movimiento> movAlbaran) {
        this.movAlbaran = movAlbaran;
    }
}