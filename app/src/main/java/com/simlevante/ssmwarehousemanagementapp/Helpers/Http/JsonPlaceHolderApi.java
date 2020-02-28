package com.simlevante.ssmwarehousemanagementapp.Helpers.Http;

import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.Models.Operacion;
import com.simlevante.ssmwarehousemanagementapp.Models.Proveedor;
import com.simlevante.ssmwarehousemanagementapp.Models.RespuestaDto;
import com.simlevante.ssmwarehousemanagementapp.Models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi
{
    @GET("Authentication/Autenticar")
    Call<Usuario> autenticar(@Query("user") String usuario, @Query("pw") String pw);

    @GET("Proveedor/ComprobarOrigen")
    Call<Proveedor> comprobarOrigen(@Query("numeroDoc") String numeroDoc);

    @GET("Operacion/ComprobarDestino")
    Call<Operacion> comprobarDestino(@Query("numeroDoc") String numeroDoc, @Query("codProv") String codProv);

    @GET("Operacion/NumeroOP")
    Call<Operacion> numeroOP(@Query("numeroDoc") String numeroDoc);

    @GET("Movimiento/LineasPdtes")
    Call<ArrayList<Movimiento>> lineasPdtes(@Query("numero") double numero);

    @POST("Operacion/NuevaOperacion")
    Call<RespuestaDto> nuevaOperacion(@Body Operacion origen, @Query("destino") double destino, @Query("numDoc") String numDoc, @Query("contadorAlb") String contadorAlb, @Query("usuario") String usuario);

    @PUT("Operacion/BloquearOperacion")
    Call<RespuestaDto> bloquearOperacion(@Query("bloquear") int bloquear, @Query("usuario") String usuario, @Query("numero") double numero);
}