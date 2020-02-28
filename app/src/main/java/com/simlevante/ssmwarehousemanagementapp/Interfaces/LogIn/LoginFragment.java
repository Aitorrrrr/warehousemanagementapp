package com.simlevante.ssmwarehousemanagementapp.Interfaces.LogIn;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
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

import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.HeaderInterceptor;
import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.JsonPlaceHolderApi;
import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.Helpers.ToastHost;
import com.simlevante.ssmwarehousemanagementapp.Interfaces.Menu.MenuFragment;
import com.simlevante.ssmwarehousemanagementapp.Models.Usuario;
import com.simlevante.ssmwarehousemanagementapp.R;

import java.util.ArrayList;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private AppCompatEditText usuario;
    private AppCompatEditText password;
    private AppCompatImageButton settings;
    private AppCompatButton acceder;

    private SettingsFragment settingsFragment;
    private MenuFragment menuFragment;

    private GuardarUsuario iGuardarUser;

    public LoginFragment()
    {

    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        usuario = v.findViewById(R.id.login_usuario_et);
        password = v.findViewById(R.id.login_password_et);

        settings = v.findViewById(R.id.login_settings);
        settings.setOnClickListener(this);

        acceder = v.findViewById(R.id.login_acceder);
        acceder.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GuardarUsuario)
        {
            iGuardarUser = (GuardarUsuario) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iGuardarUser = null;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.login_settings)
        {
            abrirPreferencias();
        }

        if (v.getId() == R.id.login_acceder)
        {
            if (comprobarCampos())
            {
                acceder();
            }
        }
    }

    private void abrirPreferencias()
    {
        settingsFragment = new SettingsFragment();
        ((NavigationHost) getActivity()).navigateTo(settingsFragment, true);
    }

    private void acceder()
    {
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String ip = userPreferences.getString(getResources().getString(R.string.setting_ip),null);
        String puerto = userPreferences.getString(getResources().getString(R.string.setting_puerto),null);
        String autenticacion = userPreferences.getString(getResources().getString(R.string.setting_auten),null);

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
            Call<Usuario> call = jsonPlaceHolderApi.autenticar(usuario.getText().toString(), password.getText().toString());

            call.enqueue(new Callback<Usuario>()
            {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response)
                {
                    if (!response.isSuccessful())
                    {
                        switch (response.code())
                        {
                            case 401:
                                ((ToastHost) getActivity()).toast("Palabra clave de autorización incorrecta.");
                                return;
                        }

                        return;
                    }

                    Usuario usuario = response.body();

                    if (usuario.getUser() == null)
                    {
                        ((ToastHost) getActivity()).toast("Usuario y/o contraseña incorrectos.");
                    }
                    else
                    {
                        ((ToastHost) getActivity()).toast("Usuario: " + usuario.getUser());
                        iGuardarUser.guardarUsuario(usuario.getUser());
                        menuFragment = new MenuFragment();
                        ((NavigationHost) getActivity()).navigateTo(menuFragment, false);
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t)
                {
                    ((ToastHost) getActivity()).toast("Parametros de conexión incorrectos o servidor no disponible.");
                }
            });
        }
    }

    private boolean comprobarCampos()
    {
        String user = usuario.getText().toString();
        String pw = password.getText().toString();

        if (user.trim().isEmpty())
        {
            ((ToastHost) getActivity()).toast("Usuario vacío.");

            return false;
        }

        if (pw.trim().isEmpty())
        {
            ((ToastHost) getActivity()).toast("Contraseña vacía.");

            return false;
        }

        return true;
    }

    public interface GuardarUsuario {
        void guardarUsuario(String Usuario);
    }
}
