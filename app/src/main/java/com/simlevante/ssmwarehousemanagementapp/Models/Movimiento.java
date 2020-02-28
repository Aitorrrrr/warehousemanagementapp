package com.simlevante.ssmwarehousemanagementapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movimiento implements Parcelable {
    private double numero;
    private double posicion;
    private String codart;
    private double incorporad;
    private double unidades;
    private double importe;
    private double iva;
    private String denominaci;
    private double tarifa;
    private double descuento;
    private double com;
    private double rec;
    private String ean13;
    private String moproviene;
    private String partida;
    private double unidadesOrigen;

    public Movimiento(Movimiento mov)
    {
        this.numero = mov.getNumero();
        this.posicion = mov.getPosicion();
        this.codart = mov.getCodart();
        this.incorporad = mov.getIncorporad();
        this.unidades = mov.getUnidades();
        this.importe = mov.getImporte();
        this.iva = mov.getIva();
        this.denominaci = mov.getDenominaci();
        this.tarifa = mov.getTarifa();
        this.descuento = mov.getDescuento();
        this.com = mov.getCom();
        this.rec = mov.getRec();
        this.ean13 = mov.getEan13();
        this.moproviene = mov.getMoproviene();
        this.unidadesOrigen = mov.getUnidadesOrigen();
        this.partida = mov.getPartida();
    }

    public double getNumero() {
        return numero;
    }

    public void setNumero(double numero) {
        this.numero = numero;
    }

    public double getPosicion() {
        return posicion;
    }

    public void setPosicion(double posicion) {
        this.posicion = posicion;
    }

    public String getCodart() {
        return codart;
    }

    public void setCodart(String codart) {
        this.codart = codart;
    }

    public double getIncorporad() {
        return incorporad;
    }

    public void setIncorporad(double incorporad) {
        this.incorporad = incorporad;
    }

    public double getUnidades() {
        return unidades;
    }

    public void setUnidades(double unidades) {
        this.unidades = unidades;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public String getDenominaci() {
        return denominaci;
    }

    public void setDenominaci(String denominaci) {
        this.denominaci = denominaci;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getCom() {
        return com;
    }

    public void setCom(double com) {
        this.com = com;
    }

    public double getRec() {
        return rec;
    }

    public void setRec(double rec) {
        this.rec = rec;
    }

    public String getEan13() {
        return ean13;
    }

    public void setEan13(String ean13) {
        this.ean13 = ean13;
    }

    public String getMoproviene() {
        return moproviene;
    }

    public void setMoproviene(String moproviene) {
        this.moproviene = moproviene;
    }

    public double getUnidadesOrigen() {
        return unidadesOrigen;
    }

    public void setUnidadesOrigen(double unidadesOrigen) {
        this.unidadesOrigen = unidadesOrigen;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    protected Movimiento(Parcel in) {
        numero = in.readDouble();
        posicion = in.readDouble();
        codart = in.readString();
        incorporad = in.readDouble();
        unidades = in.readDouble();
        importe = in.readDouble();
        iva = in.readDouble();
        denominaci = in.readString();
        tarifa = in.readDouble();
        descuento = in.readDouble();
        com = in.readDouble();
        rec = in.readDouble();
        ean13 = in.readString();
        moproviene = in.readString();
        unidadesOrigen = in.readDouble();
        partida = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(numero);
        dest.writeDouble(posicion);
        dest.writeString(codart);
        dest.writeDouble(incorporad);
        dest.writeDouble(unidades);
        dest.writeDouble(importe);
        dest.writeDouble(iva);
        dest.writeString(denominaci);
        dest.writeDouble(tarifa);
        dest.writeDouble(descuento);
        dest.writeDouble(com);
        dest.writeDouble(rec);
        dest.writeString(ean13);
        dest.writeString(moproviene);
        dest.writeDouble(unidadesOrigen);
        dest.writeString(partida);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movimiento> CREATOR = new Parcelable.Creator<Movimiento>() {
        @Override
        public Movimiento createFromParcel(Parcel in) {
            return new Movimiento(in);
        }

        @Override
        public Movimiento[] newArray(int size) {
            return new Movimiento[size];
        }
    };
}