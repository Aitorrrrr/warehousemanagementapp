package com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.HeaderInterceptor;
import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.JsonPlaceHolderApi;
import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.Helpers.ToastHost;
import com.simlevante.ssmwarehousemanagementapp.Interfaces.Menu.MenuFragment;
import com.simlevante.ssmwarehousemanagementapp.MainActivity;
import com.simlevante.ssmwarehousemanagementapp.Models.Movimiento;
import com.simlevante.ssmwarehousemanagementapp.Models.Operacion;
import com.simlevante.ssmwarehousemanagementapp.Models.Proveedor;
import com.simlevante.ssmwarehousemanagementapp.Models.RespuestaDto;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class EntradasFragment extends Fragment implements View.OnClickListener {

    private final int REQUEST_CODE = 1;

    private AppCompatEditText pedidoProv;
    private AppCompatEditText albaranProv;
    private AppCompatEditText nombreProv;

    private AppCompatButton anyadirProd;
    private AppCompatButton consultarPdtes;
    private AppCompatButton confirmarOperacion;

    private RecyclerView recycler_Creados;
    private EntradasAdaptador adaptadorCreados;
    private RecyclerView.LayoutManager rvLM;

    private ProductoFragment productoFragment;
    private PendientesFragment pendientesFragment;
    private MenuFragment menuFragment;

    private Proveedor proveedorOperacion;
    private Operacion operacionDestino;
    private Operacion operacionOrigen;

    private ArrayList<Movimiento> movPdtes;
    private ArrayList<Movimiento> movAlbaran;

    private String ip;
    private String puerto;
    private String autenticacion;
    private String contadorAlb;

    public EntradasFragment() {

    }

    public static EntradasFragment newInstance(String param1, String param2) {
        EntradasFragment fragment = new EntradasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_entradas, container, false);

        pedidoProv = v.findViewById(R.id.entradas_pedidoProv_et);
        albaranProv = v.findViewById(R.id.entradas_albaranProv_et);
        nombreProv = v.findViewById(R.id.entradas_nombreProv_et);

        anyadirProd = v.findViewById(R.id.entradas_anyadirProducto);
        consultarPdtes = v.findViewById(R.id.entradas_consultarPdtes);
        confirmarOperacion = v.findViewById(R.id.entradas_aceptarOperacion);

        anyadirProd.setOnClickListener(this);
        consultarPdtes.setOnClickListener(this);
        confirmarOperacion.setOnClickListener(this);

        preferencias();
        bloquearViews();

        if (operacionOrigen == null)
        {
            operacionOrigen = new Operacion();
        }

        if (operacionDestino == null)
        {
            operacionDestino = new Operacion();
        }

        if (movAlbaran == null)
        {
            movAlbaran = new ArrayList<>();
        }

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                int position = viewHolder.getAdapterPosition();
                Movimiento movAux = movAlbaran.get(position);

                if (movAux.getMoproviene() != null)
                {
                    if (movAux.getMoproviene().trim().compareTo("") != 0)
                    {
                        if (movAux.getUnidades() < movAux.getUnidadesOrigen())
                        {
                            boolean encontrado = false;
                            for (Movimiento movLista: movPdtes)
                            {
                                if (movAux.getPosicion() == movLista.getPosicion())
                                {
                                     encontrado = true;
                                     movLista.setIncorporad(movLista.getIncorporad() - movAux.getUnidades());
                                }
                            }

                            if (!encontrado)
                            {
                                movAux.setUnidades(movAux.getUnidadesOrigen());
                                movPdtes.add(movAux);
                            }
                        }
                        else
                        {
                            movAux.setUnidades(movAux.getUnidadesOrigen());
                            movPdtes.add(movAux);
                        }
                    }
                }

                movAlbaran.remove(position);
                adaptadorCreados.setArray(movAlbaran);
                adaptadorCreados.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        recycler_Creados = v.findViewById(R.id.entradas_movimientos_rv);
        rvLM = new LinearLayoutManager(v.getContext());
        recycler_Creados.setLayoutManager(rvLM);
        adaptadorCreados = new EntradasAdaptador(movAlbaran, getContext());
        itemTouchHelper.attachToRecyclerView(recycler_Creados);
        recycler_Creados.setAdapter(adaptadorCreados);

        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed() {
               cancelarOperacion(0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        String numDocAux = pedidoProv.getText().toString().trim();

        if (numDocAux.compareTo("") == 0)
        {
            pedirOrigen();
        }
        else
        {
            desbloquearViews();
            adaptadorCreados.setArray(movAlbaran);
            adaptadorCreados.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.entradas_anyadirProducto:
                if (movPdtes.size() > 0)
                {
                    //Obtener usuario...
                    //String usuario = ((MainActivity) getActivity()).getUsuarioGlobal();

                    productoFragment = ProductoFragment.newInstance(movPdtes);
                    productoFragment.setTargetFragment(EntradasFragment.this, REQUEST_CODE);
                    ((NavigationHost) getActivity()).navigateTo(productoFragment, true);
                }
                else
                {
                    ((ToastHost) getActivity()).toast("No quedan productos pendientes.");
                }
                break;

            case R.id.entradas_consultarPdtes:
                pendientesFragment = PendientesFragment.newInstance(movPdtes);
                ((NavigationHost) getActivity()).navigateTo(pendientesFragment, true);
                break;

            case R.id.entradas_aceptarOperacion:
                aceptarOperacion();
                break;
        }
    }

    private void pedirOrigen()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        final EditText editTextDialog = new EditText(getContext());
        dialog.setTitle("Introduzca nº pedido origen");
        dialog.setView(editTextDialog);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Aceptar", (DialogInterface dialogInterface, int which) ->
        {
            comprobarOrigen(editTextDialog.getText().toString().trim());
        });
        dialog.setNegativeButton("Cancelar", (DialogInterface dialogInterface, int which) ->
        {
            cancelarOperacion(1);
        });

        AlertDialog dialogMostrar = dialog.create();
        dialogMostrar.setOnShowListener((DialogInterface dialogInterface) ->
        {
            Button posButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
            (
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8,0,0,0);
            posButton.setLayoutParams(params);
        });

        dialogMostrar.show();
    }

    private void cancelarOperacion(int modo)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("¿Desea cancelar la operación?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Sí", (DialogInterface dialogInterface, int which) ->
        {
            bloquearOperacion(0, operacionOrigen.getNumero());
            if (operacionDestino.getNumero() != 0)
            {
                bloquearOperacion(0, operacionDestino.getNumero());
            }

            menuFragment = new MenuFragment();
            ((NavigationHost) getActivity()).navigateTo(menuFragment, false);
        });
        dialog.setNegativeButton("No", (DialogInterface dialogInterface, int which) ->
        {
            if (modo == 1)
            {
                pedirOrigen();
            }
        });

        AlertDialog dialogMostrar = dialog.create();
        dialogMostrar.setOnShowListener((DialogInterface dialogInterface) ->
        {
            Button posButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            params.setMargins(8,0,0,0);
            posButton.setLayoutParams(params);
        });
        dialogMostrar.show();
    }

    private void comprobarOrigen(String numeroDoc)
    {
        if (ip != null && puerto != null && autenticacion != null)
        {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor(autenticacion))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + ":" + puerto + "/api/")
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<Proveedor> call = jsonPlaceHolderApi.comprobarOrigen(numeroDoc);

            final ProgressDialog progressDoalog;
            progressDoalog = new ProgressDialog(getContext());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Cargando origen...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // show it
            progressDoalog.show();

            call.enqueue(new Callback<Proveedor>()
            {
                @Override
                public void onResponse(Call<Proveedor> call, Response<Proveedor> response)
                {
                    if (!response.isSuccessful())
                    {
                        switch (response.code())
                        {
                            case 401:
                                ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                progressDoalog.dismiss();
                                return;
                        }

                        progressDoalog.dismiss();
                        return;
                    }

                    proveedorOperacion = response.body();

                    if (proveedorOperacion.getNif() == null)
                    {
                        ((ToastHost) getActivity()).toast("Operación origen no encontrada o en uso.");
                        cancelarOperacion(1);
                    }
                    else
                    {
                        // Operación origen existe, procedemos a pedir nº albarán
                        pedidoProv.setText(numeroDoc);
                        nombreProv.setText(proveedorOperacion.getNombre());
                        numeroOP(numeroDoc);
                        pedirAlbaran();
                    }

                    progressDoalog.dismiss();
                }

                @Override
                public void onFailure(Call<Proveedor> call, Throwable t)
                {
                    ((ToastHost) getActivity()).toast("Parametros de conexión incorrectos o servidor no disponible.");
                    progressDoalog.dismiss();
                }
            });
        }
    }

    private void bloquearViews()
    {
        albaranProv.setEnabled(false);
        anyadirProd.setEnabled(false);
        consultarPdtes.setEnabled(false);
        confirmarOperacion.setEnabled(false);
        pedidoProv.setEnabled(false);
    }

    private void desbloquearViews()
    {
        anyadirProd.setEnabled(true);
        consultarPdtes.setEnabled(true);
        confirmarOperacion.setEnabled(true);
    }

    private void pedirAlbaran()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        final EditText editTextDialog = new EditText(getContext());
        dialog.setTitle("Introducir o generar nº albarán destino");
        dialog.setView(editTextDialog);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Introducir", (DialogInterface dialogInterface, int which) ->
        {
            // Comprobamos si destino existe, esta facturado, etx
            if (editTextDialog.getText().toString().trim().compareTo("") == 0)
            {
                if (contadorAlb.trim().compareTo("") == 0)
                {
                    ((ToastHost) getActivity()).toast("Contador para albaranes de compra no definido. Introduzca un documento destino.");
                    pedirAlbaran();
                    return;
                }
                else
                {
                    ((ToastHost) getActivity()).toast("Albarán destino vacío, se creará una nueva operación.");
                }
            }
            else
            {
                comprobarDestino(editTextDialog.getText().toString().trim());
            }

            // Cargamos líneas origen y desbloqueamos funcionalidad
            cargarLineasPdtes();
            desbloquearViews();
        });
        dialog.setNegativeButton("Generar", (DialogInterface dialogInterface, int which) ->
        {
            // Cargamos líneas origen y desbloqueamos funcionalidad
            if (contadorAlb.trim().compareTo("") == 0)
            {
                ((ToastHost) getActivity()).toast("Contador para albaranes de compra no definido. Introduzca un documento destino.");
                pedirAlbaran();
            }
            else
            {
                cargarLineasPdtes();
                desbloquearViews();
            }
        });

        AlertDialog dialogMostrar = dialog.create();
        dialogMostrar.setOnShowListener((DialogInterface dialogInterface) ->
        {
            Button posButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            params.setMargins(8,0,0,0);
            posButton.setLayoutParams(params);
        });
        dialogMostrar.show();
    }

    private void comprobarDestino(String numeroDoc)
    {
        if (ip != null && puerto != null && autenticacion != null)
        {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor(autenticacion))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + ":" + puerto + "/api/")
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<Operacion> call = jsonPlaceHolderApi.comprobarDestino(numeroDoc, proveedorOperacion.getNif());

            final ProgressDialog progressDoalog;
            progressDoalog = new ProgressDialog(getContext());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Buscando destino...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // show it
            progressDoalog.show();

            call.enqueue(new Callback<Operacion>()
            {
                @Override
                public void onResponse(Call<Operacion> call, Response<Operacion> response)
                {
                    if (!response.isSuccessful())
                    {
                        switch (response.code())
                        {
                            case 401:
                                ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                progressDoalog.dismiss();
                                return;
                        }

                        progressDoalog.dismiss();
                        return;
                    }

                    operacionDestino = response.body();

                    if (operacionDestino.getNumeroDoc() == null)
                    {
                        // Operación destino no existe.
                        progressDoalog.dismiss();
                        operacionDestino.setNumeroDoc(numeroDoc);
                        albaranProv.setText(numeroDoc);
                        return;
                    }

                    if (operacionDestino.getPendientes() > 0)
                    {
                        // Operación destino existe, y no esta facturado totalmente
                        progressDoalog.dismiss();
                        ((ToastHost) getActivity()).toast("Operación destino aceptada.");
                        operacionDestino.setNumeroDoc(numeroDoc);
                        albaranProv.setText(numeroDoc);
                        bloquearOperacion(1, operacionDestino.getNumero());
                        return;
                    }

                    if (operacionDestino.getPendientes() == 0)
                    {
                        // Operación destino existe, y esta facturado totalmente
                        progressDoalog.dismiss();
                        ((ToastHost) getActivity()).toast("Operación destino facturada, se creará una nueva operación.");
                        albaranProv.setText("");
                        return;
                    }

                    progressDoalog.dismiss();
                }

                @Override
                public void onFailure(Call<Operacion> call, Throwable t)
                {
                    ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                    progressDoalog.dismiss();
                }
            });
        }
    }

    private void numeroOP(String numeroDoc)
    {
        if (ip != null && puerto != null && autenticacion != null)
        {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor(autenticacion))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + ":" + puerto + "/api/")
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<Operacion> call = jsonPlaceHolderApi.numeroOP(numeroDoc);

            final ProgressDialog progressDoalog;
            progressDoalog = new ProgressDialog(getContext());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Buscando operación...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // show it
            progressDoalog.show();

            call.enqueue(new Callback<Operacion>()
            {
                @Override
                public void onResponse(Call<Operacion> call, Response<Operacion> response)
                {
                    if (!response.isSuccessful())
                    {
                        switch (response.code())
                        {
                            case 401:
                                ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                progressDoalog.dismiss();
                                return;
                        }

                        progressDoalog.dismiss();
                        return;
                    }

                    operacionOrigen = response.body();
                    operacionOrigen.setNumeroDoc(pedidoProv.getText().toString());
                    bloquearOperacion(1, operacionOrigen.getNumero());

                    progressDoalog.dismiss();
                }

                @Override
                public void onFailure(Call<Operacion> call, Throwable t)
                {
                    ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                    progressDoalog.dismiss();
                }
            });
        }
    }

    private void cargarLineasPdtes()
    {
        if (ip != null && puerto != null && autenticacion != null)
        {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor(autenticacion))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + ":" + puerto + "/api/")
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<ArrayList<Movimiento>> call = jsonPlaceHolderApi.lineasPdtes(operacionOrigen.getNumero());

            final ProgressDialog progressDoalog;
            progressDoalog = new ProgressDialog(getContext());
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Buscando operación...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // show it
            progressDoalog.show();

            call.enqueue(new Callback<ArrayList<Movimiento>>()
            {
                @Override
                public void onResponse(Call<ArrayList<Movimiento>> call, Response<ArrayList<Movimiento>> response)
                {
                    if (!response.isSuccessful())
                    {
                        switch (response.code())
                        {
                            case 401:
                                ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                progressDoalog.dismiss();
                                return;
                        }

                        progressDoalog.dismiss();
                        return;
                    }

                    movPdtes = response.body();

                    for (Movimiento mov: movPdtes)
                    {
                        mov.setUnidadesOrigen(mov.getUnidades());
                    }

                    progressDoalog.dismiss();
                }

                @Override
                public void onFailure(Call<ArrayList<Movimiento>> call, Throwable t)
                {
                    ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                    progressDoalog.dismiss();
                }
            });
        }
    }

    private void aceptarOperacion()
    {
        if (movAlbaran.size() > 0)
        {
            operacionOrigen.setMovAlbaran(movAlbaran);

            if (ip != null && puerto != null && autenticacion != null)
            {
                //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient cliente = new OkHttpClient.Builder()
                        //.addInterceptor(interceptor)
                        .addInterceptor(new HeaderInterceptor(autenticacion))
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ip + ":" + puerto + "/api/")
                        .client(cliente)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                String usuario = ((MainActivity) getActivity()).getUsuarioGlobal();
                if (usuario == null)
                {
                    usuario = "";
                }

                String numDocDest = "";
                if (operacionDestino.getNumeroDoc() != null)
                {
                    numDocDest = operacionDestino.getNumeroDoc();
                }

                Call<RespuestaDto> call = jsonPlaceHolderApi.nuevaOperacion(operacionOrigen, operacionDestino.getNumero(), numDocDest, contadorAlb, usuario);

                final ProgressDialog progressDoalog;
                progressDoalog = new ProgressDialog(getContext());
                progressDoalog.setMax(100);
                progressDoalog.setMessage("Creando operación...");
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                progressDoalog.show();

                call.enqueue(new Callback<RespuestaDto>()
                {
                    @Override
                    public void onResponse(Call<RespuestaDto> call, Response<RespuestaDto> response)
                    {
                        if (!response.isSuccessful())
                        {
                            switch (response.code())
                            {
                                case 401:
                                    ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                    progressDoalog.dismiss();
                                    return;
                            }

                            progressDoalog.dismiss();
                            return;
                        }

                        RespuestaDto resultado = response.body();

                        progressDoalog.dismiss();

                        ((ToastHost) getActivity()).toast(resultado.getRespuesta());

                        if (resultado.getError() == 0)
                        {
                            bloquearOperacion(0, operacionOrigen.getNumero());
                            if (operacionDestino.getNumero() != 0)
                            {
                                bloquearOperacion(0, operacionDestino.getNumero());
                            }

                            menuFragment = new MenuFragment();
                            ((NavigationHost) getActivity()).navigateTo(menuFragment, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaDto> call, Throwable t)
                    {
                        ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                        progressDoalog.dismiss();
                    }
                });
            }
        }
        else
        {
            ((ToastHost) getActivity()).toast("No se ha incluido ninguna línea en la operación.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE){
                ArrayList<Movimiento> movimientosRecibidos = data.getParcelableArrayListExtra(getResources().getString(R.string.entradas_intercambio_movCreado));
                Movimiento movBorrar = data.getParcelableExtra(getResources().getString(R.string.entradas_intercambio_movBorrar));
                Double udsIncorporadas = data.getDoubleExtra(getResources().getString(R.string.entradas_intercambio_movUdsIncorp), 0);

                movAlbaran.addAll(movimientosRecibidos);

                if ((movBorrar.getUnidades() - movBorrar.getIncorporad()) > udsIncorporadas)
                {
                    movPdtes.remove(movBorrar);
                    movBorrar.setIncorporad(movBorrar.getIncorporad() + udsIncorporadas);
                    movPdtes.add(movBorrar);
                }
                else
                {
                    movPdtes.remove(movBorrar);
                }
            }
        }
    }

    private void preferencias()
    {
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        ip = userPreferences.getString(getResources().getString(R.string.setting_ip),"");
        puerto = userPreferences.getString(getResources().getString(R.string.setting_puerto),"");
        autenticacion = userPreferences.getString(getResources().getString(R.string.setting_auten),"");
        contadorAlb = userPreferences.getString(getResources().getString(R.string.setting_serie_compra),"");

        if (ip.isEmpty())
        {
            ((ToastHost) getActivity()).toast("Parámetro IP vacío.");
            return;
        }
        if (puerto.isEmpty())
        {
            ((ToastHost) getActivity()).toast("Parámetro puerto vacío.");
            return;
        }
        if (autenticacion.isEmpty())
        {
            ((ToastHost) getActivity()).toast("Parámetro palabra de autenticación vacío.");
            return;
        }
    }

    private void bloquearOperacion(int bloquear, double numero)
    {
        OkHttpClient cliente = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(autenticacion))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + ":" + puerto + "/api/")
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        String usuario = ((MainActivity) getActivity()).getUsuarioGlobal();
        if (usuario == null)
        {
            usuario = "";
        }

        Call<RespuestaDto> call = jsonPlaceHolderApi.bloquearOperacion(bloquear, usuario, numero);

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Bloqueos/Desbloqueos...");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDoalog.show();

        call.enqueue(new Callback<RespuestaDto>()
        {
            @Override
            public void onResponse(Call<RespuestaDto> call, Response<RespuestaDto> response)
            {
                if (!response.isSuccessful())
                {
                    switch (response.code())
                    {
                        case 401:
                            ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                            progressDoalog.dismiss();
                            return;
                    }

                    progressDoalog.dismiss();
                    return;
                }

                RespuestaDto resultado = response.body();

                progressDoalog.dismiss();
            }

            @Override
            public void onFailure(Call<RespuestaDto> call, Throwable t)
            {
                ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                progressDoalog.dismiss();
            }
        });
    }
}