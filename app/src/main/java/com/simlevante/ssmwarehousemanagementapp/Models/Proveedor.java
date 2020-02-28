package com.simlevante.ssmwarehousemanagementapp.Models;

public class Proveedor
{
    private String exentoIva;
    private String nombre;
    private String nif;

    public String getExentoIva() {
        return exentoIva;
    }

    public void setExentoIva(String exentoIva) {
        this.exentoIva = exentoIva;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }
}
