package com.simlevante.ssmwarehousemanagementapp.Models;

public class RespuestaDto
{
    private int error;
    private String respuesta;

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}