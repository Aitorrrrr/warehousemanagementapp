package com.simlevante.ssmwarehousemanagementapp.Interfaces.LogIn;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.HeaderInterceptor;
import com.simlevante.ssmwarehousemanagementapp.Helpers.Http.JsonPlaceHolderApi;
import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.Helpers.ToastHost;
import com.simlevante.ssmwarehousemanagementapp.Interfaces.Menu.MenuFragment;
import com.simlevante.ssmwarehousemanagementapp.Models.Usuario;
import com.simlevante.ssmwarehousemanagementapp.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private AppCompatEditText usuario;
    private AppCompatEditText password;
    private AppCompatImageButton settings;
    private AppCompatButton acceder;
    private ImageView iv;

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
        iv = v.findViewById(R.id.login_imageview);

        usuario = v.findViewById(R.id.login_usuario_et);
        password = v.findViewById(R.id.login_password_et);

        settings = v.findViewById(R.id.login_settings);
        settings.setOnClickListener(this);

        acceder = v.findViewById(R.id.login_acceder);
        acceder.setOnClickListener(this);

        iv.setBackground(resizeImage(R.drawable.fabric_wallpaper));

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
                    ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o servidor no disponible.");
                }
            });
        }
        else
        {
            ((ToastHost) getActivity()).toast("Parámetros de conexión incorrectos o incompletos.");
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

    public Drawable resizeImage(int imageResource)
    {
        // Get device dimensions
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        double deviceWidth = displaymetrics.widthPixels;

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
                imageResource,null);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageResource);
        Drawable drawable = new BitmapDrawable(this.getResources(),
                getResizedBitmap(bMap, newImageHeight, (int) deviceWidth));

        return drawable;
    }

    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
                                   int bitmapHeight)
    {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight,
                true);
    }
}