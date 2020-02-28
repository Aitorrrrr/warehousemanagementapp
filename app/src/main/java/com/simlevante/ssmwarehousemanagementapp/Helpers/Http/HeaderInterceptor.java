package com.simlevante.ssmwarehousemanagementapp.Helpers.Http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor
{
    private String autenticacion;

    public HeaderInterceptor(String autenticacion)
    {
        this.autenticacion = autenticacion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request()
                            .newBuilder()
                            .addHeader("X-API-KEY", "ApiKey " + autenticacion).build();

        return chain.proceed(request);
    }
}
